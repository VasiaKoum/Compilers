import visitor.GJDepthFirst;
import syntaxtree.*;
import java.util.*;
import java.io.*;

public class GenCode extends GJDepthFirst<String, String>{
    SymbolTable symboltable;
    FileWriter llfile;
    int regnum;

    public GenCode(SymbolTable st, FileWriter file){
        this.symboltable = st;
        this.llfile = file;
        this.regnum = 0;

        initialize_ll();
    }

    public void initialize_ll(){
        String buff = "";
        // FIX THIS-> vtable with order(the offsets)???
        // FIX THIS-> main in MainClass DELETE IT
        for (String keyclass : symboltable.classes.keySet()) {
            HashMap<String, Methods> methodmap = new HashMap<String, Methods>();
            String parentname, nameclass;
            nameclass = symboltable.classes.get(keyclass).name;
            parentname = nameclass;
            while(parentname!=null){
                for (String keymethod : symboltable.methods.keySet()) {
                    if(symboltable.methods.get(keymethod).classpar.equals(symboltable.classes.get(parentname).name)){
                        Methods method = symboltable.methods.get(keymethod);
                        if(!methodmap.containsKey(method.name)) {

                            methodmap.put(method.name, method);
                        }
                        else{
                            methodmap.replace(method.name, method);
                        }
                    }
                }
                parentname = symboltable.classes.get(parentname).parent;
            }
            int argsnum = 1;
            buff = "@."+keyclass+"_vtable = global ["+methodmap.size()+" x i8*] [";
            for (String keymap : methodmap.keySet()) {
                Methods method_ = methodmap.get(keymap);
                // FIX THIS-> return only int or boolean?
                if(method_.type == "int") buff = buff+"\n\ti8* bitcast (i32 (i8*";
                else if(method_.type == "boolean") buff = buff+"\n\ti8* bitcast (i1 (i8*";
                for (String keyargs : method_.args.keySet()) {
                    Variables arg_ = method_.args.get(keyargs);
                    // FIX THIS-> what are the offsets for args: int[], boolean[], obj
                    if(arg_.type == "int") buff = buff+", i32";
                    else if(arg_.type == "boolean") buff = buff+", i1";
                    else if(arg_.type == "int[]") buff = buff+", i32*";
                    else buff = buff+", i8*";
                    // System.out.println(arg_.name+" : "+arg_.type);
                }
                buff = buff+")* @"+method_.classpar+"."+method_.name+" to i8*)";
                if(argsnum<methodmap.size()) buff = buff+",";
                else buff = buff+"\n";
                argsnum+=1;
            }
            buff = buff+"]\n\n";
            write_to_ll(buff);
        }
        buff = "declare i8* @calloc(i32, i32)\n";
        buff = buff+"declare i32 @printf(i8*, ...)\n";
        buff = buff+"declare void @exit(i32)\n\n";
        buff = buff+"@_cint = constant [4 x i8] c\"%d\\0a\\00\"\n";
        buff = buff+"@_cOOB = constant [15 x i8] c\"Out of bounds\\0a\\00\"\n";
        buff = buff+"@_cNSZ = constant [15 x i8] c\"Negative size\\0a\\00\"\n";
        buff = buff+"\ndefine void @print_int(i32 %i) {\n\t%_str = bitcast [4 x i8]* @_cint to i8*\n\tcall i32 (i8*, ...) @printf(i8* %_str, i32 %i)\n\tret void\n}\n";
        buff = buff+"\ndefine void @throw_oob() {\n\t%_str = bitcast [15 x i8]* @_cOOB to i8*\n\tcall i32 (i8*, ...) @printf(i8* %_str)\n\tcall void @exit(i32 1)\n\tret void\n}\n";
        buff = buff+"\ndefine void @throw_nsz() {\n\t%_str = bitcast [15 x i8]* @_cNSZ to i8*\n\tcall i32 (i8*, ...) @printf(i8* %_str)\n\tcall void @exit(i32 1)\n\tret void\n}\n";
        write_to_ll(buff);
    }

    public void write_to_ll(String buffer){
        try{
            llfile.write(buffer);
        }
        catch (IOException e) {
            System.out.println("Error at writing the <file>.ll");
            e.printStackTrace();
        }
    }
}
