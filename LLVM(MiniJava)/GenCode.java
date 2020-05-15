import visitor.GJDepthFirst;
import syntaxtree.*;
import java.util.*;
import java.io.*;

public class GenCode extends GJDepthFirst<String, String>{
    SymbolTable symboltable;
    // LinkedHashMap<String, Variables> vtable;
    String register;
    FileWriter llfile;
    int regnum;

    public GenCode(SymbolTable st, FileWriter file){
        this.symboltable = st;
        this.llfile = file;
        this.regnum = 0;
        this.register = "";
        initialize_ll();
    }

    public String visit(NodeToken n, String argu) { return n.toString(); }

    public String visit(MainClass n, String argu) {
        write_to_ll("\ndefine i32 @main(){\n");
        String nameclass = n.f1.accept(this, argu);
        n.f14.accept(this, argu);
        n.f15.accept(this, argu);
        write_to_ll("\n\tret i32 0\n}\n");
        return null;
    }

    public String visit(VarDeclaration n, String argu) {
        String buff;
        String name = n.f1.accept(this, argu);
        if(!"Class".equals(symboltable.scope)){
            // vtable.put(symboltable.currentclass.name+symboltable.currentmethod.name+name, new Variables());
            buff = "\t%"+name+" = alloca "+typeinbytes(n.f0.accept(this, argu))+"\n";
            write_to_ll(buff);
        }
        return name;
    }

    public String visit(ClassDeclaration n, String argu) {
        String name = n.f1.accept(this, argu);
        symboltable.currentclass = new Classes(name, null);
        symboltable.scope = "Class";
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        return null;
    }

    public String visit(ClassExtendsDeclaration n, String argu) {
        String name, parentname;
        name = n.f1.accept(this, argu);
        parentname = n.f3.accept(this, argu);
        symboltable.currentclass = new Classes(name, parentname);
        symboltable.scope = "Class";
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        return null;
    }

    /**
     * f1 -> Type()
     * f2 -> Identifier()
     * f4 -> ( FormalParameterList() )?
     * f7 -> ( VarDeclaration() )*
     * f8 -> ( Statement() )*
     * f9 -> "return"
     * f10 -> Expression()
     */
    public String visit(MethodDeclaration n, String argu) {
        String name = n.f2.accept(this, argu);
        String type = n.f1.accept(this, argu);
        regnum = 0;
        write_to_ll("\ndefine "+typeinbytes(type)+" @"+symboltable.currentclass.name+"."+name+"(i8* %this");
        symboltable.currentmethod = new Methods(name, symboltable.currentclass.name, type);
        n.f4.accept(this, argu);
        write_to_ll(") {\n");
        allocationvars();
        n.f7.accept(this, argu);
        n.f8.accept(this, argu);
        n.f10.accept(this, type);
        write_to_ll("\tret "+typeinbytes(type)+" %_"+regnum+"\n}\n");
        return null;
    }

    public String visit(FormalParameterList n, String argu) {
       String _ret=null;
       write_to_ll(", ");
       n.f0.accept(this, argu);
       n.f1.accept(this, argu);
       return null;
    }

    public String visit(FormalParameter n, String argu) {
        String name = n.f1.accept(this, argu);
        String type = n.f0.accept(this, argu);
        write_to_ll(typeinbytes(type)+" %."+name);
        symboltable.scope = "Args";
        return null;
    }

    public String visit(FormalParameterTerm n, String argu) {
        write_to_ll(", ");
        n.f1.accept(this, argu);
        return null;
    }

    public String visit(Identifier n, String argu) {
        String name = n.f0.accept(this, argu);
        Variables idvar;
        if(argu!=null){
            // System.out.println("YES: "+name+" ARGU IS "+argu);
            if((idvar = symboltable.findvar_vtable(symboltable.currentclass.name, symboltable.currentmethod.name, name))!=null){
                System.out.println("NO: "+name);
                register = "%_"+regnum;
                write_to_ll("\t%_"+regnum+" = load "+typeinbytes(idvar.type)+", "+typeinbytes(idvar.type)+"* %"+idvar.name+"\n");
                name =  register;
                regnum+=1;
            }
        }
        return name;
    }

    //
    // public String visit(TrueLiteral n, String argu) {
    //     n.f0.accept(this, argu);
    //     return "boolean";
    // }
    //
    // public String visit(FalseLiteral n, String argu) {
    //     n.f0.accept(this, argu);
    //     return "boolean";
    // }

    public String visit(Expression n, String argu) {
        write_to_ll("\n");
        return n.f0.accept(this, argu);
    }

    public String visit(PlusExpression n, String argu) {
        String reg1 = n.f0.accept(this, "int");
        String reg2 = n.f2.accept(this, "int");
        String ret = "%_"+regnum;
        write_to_ll( "\t%_"+regnum+" = add i32 "+reg1+", "+reg2+"\n");
        regnum+=1;
        return ret;
    }

    public String visit(MinusExpression n, String argu) {
        String reg1 = n.f0.accept(this, "int");
        String reg2 = n.f2.accept(this, "int");
        String ret = "%_"+regnum;
        write_to_ll( "\t%_"+regnum+" = sub i32 "+reg1+", "+reg2+"\n");
        regnum+=1;
        return ret;
    }

    public String visit(TimesExpression n, String argu) {
        String reg1 = n.f0.accept(this, "int");
        String reg2 = n.f2.accept(this, "int");
        String ret = "%_"+regnum;
        write_to_ll( "\t%_"+regnum+" = mul i32 "+reg1+", "+reg2+"\n");
        regnum+=1;
        return ret;
    }

    public String visit(PrintStatement n, String argu) {
        String _ret=null;
        String regexpr = n.f2.accept(this, argu);
        write_to_ll("\tcall void (32) @print_int(i32 %"+regexpr+")\n");
        return null;
    }

    public String typeinbytes(String type){
        String returned="";
        if("int".equals(type)) returned = "i32";
        else if("boolean".equals(type)) returned = "i1";
        else if("int[]".equals(type)) returned = "i32*";
        else returned = "i8*";
        return returned;
    }

    public void allocationvars(){
        Methods method = symboltable.methods.get(symboltable.currentclass.name+symboltable.currentmethod.name);
        String buff="";
        for (String keyargs : method.args.keySet()) {
            Variables var = method.args.get(keyargs);
            String vartype = typeinbytes(var.type);
            buff = buff+"\t%"+var.name+" = alloca "+vartype+"\n";
            buff = buff+"\tstore "+vartype+" %."+var.name+", "+vartype+"* %"+var.name+"\n";
        }
        write_to_ll(buff);
    }

    public void initialize_ll(){
        String buff = "";
        boolean mainclass=true;
        for (String keyclass : symboltable.classes.keySet()) {
            HashMap<String, Methods> methodmap = new HashMap<String, Methods>();
            String parentname, nameclass;
            nameclass = symboltable.classes.get(keyclass).name;
            parentname = nameclass;
            while(parentname!=null){
                for (String keymethod : symboltable.methods.keySet()) {
                    if(symboltable.methods.get(keymethod).classpar.equals(symboltable.classes.get(parentname).name)){
                        Methods method = symboltable.methods.get(keymethod);
                        if(!methodmap.containsKey(method.name)) methodmap.put(method.name, method);
                    }
                }
                parentname = symboltable.classes.get(parentname).parent;
            }
            int argsnum = 1;
            if(!mainclass){
                buff = "@."+keyclass+"_vtable = global ["+methodmap.size()+" x i8*] [";
                // List<Methods> sortedmap = new ArrayList<>(methodmap.values());
                // Collections.sort(sortedmap, Comparator.comparing(Methods::getOffset));
                for (String keymethod : methodmap.keySet()) {
                    Methods method_ = methodmap.get(keymethod);
                    buff = buff+"\n\ti8* bitcast ("+typeinbytes(method_.type)+" (i8*";
                    for (String keyargs : method_.args.keySet()) {
                        Variables arg_ = method_.args.get(keyargs);
                        buff = buff +", "+typeinbytes(arg_.type);
                    }
                    if(symboltable.methods.containsKey(keyclass+method_.name)) symboltable.methods.get(keyclass+method_.name).vplace = argsnum-1;
                    else{
                        method_.vplace = argsnum-1;
                        symboltable.methods.put(keyclass+method_.name, method_);
                    }
                    // System.out.println(method_.classpar+"."+method_.name+" : "+method_.offset);
                    buff = buff+")* @"+method_.classpar+"."+method_.name+" to i8*)";
                    if(argsnum<methodmap.size()) buff = buff+",";
                    else buff = buff+"\n";
                    argsnum+=1;
                }
            }
            else{
                buff = "@."+keyclass+"_vtable = global [0 x i8*] [";
                mainclass = false;
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
