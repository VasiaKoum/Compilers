import visitor.GJDepthFirst;
import syntaxtree.*;
import java.util.*;
import java.io.*;

public class GenCode extends GJDepthFirst<String, String>{
    SymbolTable symboltable;
    String register, currtype;
    LinkedHashMap<String, HashMap<String, Methods>> vtable;
    Variables var;
    FileWriter llfile;
    Boolean storevar;
    int regnum;

    public GenCode(SymbolTable st, FileWriter file){
        this.symboltable = st;
        this.llfile = file;
        this.regnum = 0;
        this.register = "";
        this.storevar = false;
        this.currtype = "";
        this.var = null;
        this.vtable = new LinkedHashMap<String, HashMap<String, Methods>>();
        initialize_ll();
    }

    public String visit(NodeToken n, String argu) { return n.toString(); }

    public String visit(MainClass n, String argu) {
        write_to_ll("\ndefine i32 @main(){\n");
        String nameclass = n.f1.accept(this, argu);
        symboltable.currentclass = new Classes(nameclass, null);
        symboltable.currentmethod = new Methods("main", nameclass, "void");

        n.f14.accept(this, argu);
        n.f15.accept(this, argu);
        write_to_ll("\n\tret i32 0\n}\n");
        return null;
    }

    public String visit(VarDeclaration n, String argu) {
        String buff;
        String name = n.f1.accept(this, argu);
        if(!"Class".equals(symboltable.scope)){
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
        String ret = n.f10.accept(this, type);
        write_to_ll("\tret "+typeinbytes(type)+" "+ret+"\n}\n");
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

    public String visit(AssignmentStatement n, String argu) {
        storevar = false;
        String reg2 = n.f2.accept(this, " "), type1=null;
        storevar = true;
        String reg1 = n.f0.accept(this, " ");
        storevar = false;
        String type = typeinbytes(currtype);
        write_to_ll("\tstore "+type+" "+reg2+", "+type+"* "+reg1+"\n");
        return null;
    }

    public String visit(Identifier n, String argu) {
        String name = n.f0.accept(this, argu);
        String buff="", typevar;
        if(argu!=null){
            if((var = symboltable.findvar(symboltable.currentclass.name, symboltable.currentmethod.name, name, true))!=null){
                typevar = typeinbytes(var.type);
                Classes findin = symboltable.inclass;
                if(findin!=null) {
                    int offset = 8+findin.vars.get(var.name).offset;
                    buff = buff+"\t%_"+regnum+" = getelementptr i8, i8* %this, i32 "+offset+"\n"; regnum+=1;
                    buff = buff+"\t%_"+regnum+" = bitcast i8* %_"+(regnum-1)+" to "+typeinbytes(var.type)+"*\n";
                    write_to_ll(buff);
                    typevar = typeinbytes(var.type);
                    register = "%_"+regnum;
                    name = register;
                    regnum+=1;
                }
                else name = "%"+name;
                if(!storevar){
                    write_to_ll("\t%_"+regnum+" = load "+typevar+", "+typevar+"* "+name+"\n");
                    name = "%_"+regnum;
                    regnum+=1;
                }
                currtype = var.type;
            }
        }
        return name;
    }

    public String visit(TrueLiteral n, String argu) {
        return "1";
    }

    public String visit(FalseLiteral n, String argu) {
        return "0";
    }

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

    public String visit(AllocationExpression n, String argu) {
        String name = n.f1.accept(this, argu), regret=null, buff;
        HashMap<String, Methods> methodmap = vtable.get(name);
        int regcast = regnum, regcall = regnum;
        if(methodmap!=null){
            int sizeOffset = symboltable.sizeClass(name)+8;
            buff = "\t%_"+regnum+" = call i8* @calloc(i32 1, i32 "+sizeOffset+")\n"; regnum+=1;
            buff = buff+"\t%_"+regnum+" = bitcast i8* %_"+(regnum-1)+" to i8***\n"; regnum+=1;
            buff = buff+"\t%_"+regnum+" = getelementptr ["+vtable.get(name).size()+" x i8*], ["+vtable.get(name).size()+" x i8*]* @."+name+"_vtable, i32 0, i32 0\n"+
            "\tstore i8** %_"+regnum+", i8*** %_"+(regnum-1)+"\n"; regnum+=1;
            currtype = name;
            write_to_ll(buff);
        }
        return "%_"+(regnum-3);
    }

    public String visit(MessageSend n, String argu) {
       String reg = n.f0.accept(this, argu);
       Methods method;
       int register;
       String buff = "\t%_"+regnum+" = bitcast i8* "+reg+" to i8***\n"; regnum+=1;
       buff = buff+"\t%_"+regnum+" = load i8**, i8*** %_"+(regnum-1)+"\n"; regnum+=1;
       String name = n.f2.accept(this, null);
       int position = vtable.get(currtype).get(name).offset/8;
       method = vtable.get(currtype).get(name);
       buff = buff+"\t%_"+regnum+" = getelementptr i8*, i8** %_"+(regnum-1)+", i32 "+position+"\n"; regnum+=1;
       buff = buff+"\t%_"+regnum+" = load i8*, i8** %_"+(regnum-1)+"\n"; regnum+=1;
       buff = buff+"\t%_"+regnum+" = bitcast i8* %_"+(regnum-1)+" to "+typeinbytes(method.type)+" ("+argsinbytes(method)+")*"+"\n"; regnum+=1;
       register = regnum-1;
       write_to_ll(buff);
       //f4 -> ( ExpressionList() )?
       n.f4.accept(this, argu);
       buff = "\t%_"+regnum+" = call "+typeinbytes(method.type)+" %_"+register+"(i8* "+reg+")\n";
       register = regnum; regnum+=1;
       write_to_ll(buff);
       return "%_"+register;
    }

    /**
     * f0 -> Expression()
     * f1 -> ExpressionTail()
     */
    public String visit(ExpressionList n, String argu) {
       String expr = n.f0.accept(this, argu);
       String exprt = n.f1.accept(this, argu);
       System.out.println("ExpressionList: "+expr+" "+expr);
       return null;
    }

    /**
     * f0 -> ","
     * f1 -> Expression()
     */
    public String visit(ExpressionTerm n, String argu) {
       String expr = n.f1.accept(this, argu);
       System.out.println("ExpressionTerm: "+expr);
       return null;
    }

    /**
     * f0 -> "("
     * f1 -> Expression()
     * f2 -> ")"
     */
    public String visit(BracketExpression n, String argu) {
       String _ret=null;
       String reg = n.f1.accept(this, argu);
       System.out.println("brackets: "+reg+" currtype "+currtype);
       return reg;
    }

    public String visit(PrintStatement n, String argu) {
        String _ret=null;
        String regexpr = n.f2.accept(this, " ");
        write_to_ll("\tcall void (i32) @print_int(i32 "+regexpr+")\n");
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
            var = method.args.get(keyargs);
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
            LinkedHashMap<String, Methods> methodmap = new LinkedHashMap<String, Methods>();
            String parentname, nameclass;
            nameclass = symboltable.classes.get(keyclass).name;
            parentname = nameclass;
            createVtable(methodmap, parentname, keyclass);
            int argsnum = 1;
            if(!mainclass){
                buff = "@."+keyclass+"_vtable = global ["+methodmap.size()+" x i8*] [";
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
            vtable.put(keyclass, methodmap);
        }
        buff = "declare i8* @calloc(i32, i32)\n"+"declare i32 @printf(i8*, ...)\n"+"declare void @exit(i32)\n\n"+
        "@_cint = constant [4 x i8] c\"%d\\0a\\00\"\n"+"@_cOOB = constant [15 x i8] c\"Out of bounds\\0a\\00\"\n"+"@_cNSZ = constant [15 x i8] c\"Negative size\\0a\\00\"\n"+
        "\ndefine void @print_int(i32 %i) {\n\t%_str = bitcast [4 x i8]* @_cint to i8*\n\tcall i32 (i8*, ...) @printf(i8* %_str, i32 %i)\n\tret void\n}\n"+
        "\ndefine void @throw_oob() {\n\t%_str = bitcast [15 x i8]* @_cOOB to i8*\n\tcall i32 (i8*, ...) @printf(i8* %_str)\n\tcall void @exit(i32 1)\n\tret void\n}\n"+
        "\ndefine void @throw_nsz() {\n\t%_str = bitcast [15 x i8]* @_cNSZ to i8*\n\tcall i32 (i8*, ...) @printf(i8* %_str)\n\tcall void @exit(i32 1)\n\tret void\n}\n";
        write_to_ll(buff);
    }

    public String argsinbytes(Methods method){
        String buff="i8*,";
        for (String keyarg : method.args.keySet()) {
            buff=buff+typeinbytes(method.args.get(keyarg).type)+",";
        }
        buff=buff.substring(0, buff.length()-1);
        return buff;
    }

    public void createVtable(LinkedHashMap<String, Methods> methodmap, String parentname, String keyclass){
        String name;
        if(parentname!=null) {
            name=parentname;
            parentname = symboltable.classes.get(parentname).parent;
            createVtable(methodmap, parentname, keyclass);
            for (String keymethod : symboltable.methods.keySet()) {
                // System.out.println(keyclass+" "+symboltable.methods.get(keymethod).classpar+"."+symboltable.methods.get(keymethod).name);
                if(symboltable.methods.get(keymethod).classpar.equals(name)){
                    Methods method = symboltable.methods.get(keymethod);
                    methodmap.put(method.name, method);
                }
            }
        }
    }

    // public int functionIndex(String keyclass, String ){
    //
    // }

    public void write_to_ll(String buffer){
        try{
            llfile.write(buffer);
        }
        catch (IOException e) {
            System.out.println("Error at writing the <file>.ll");
            e.printStackTrace();
        }
    }

    public void printHasmap(){
        for (String keyclass : symboltable.classes.keySet()) {
            HashMap<String, Methods> methodmap = vtable.get(keyclass);
            for (String keymethod : methodmap.keySet()) {
                Methods method_ = methodmap.get(keymethod);
                System.out.println(keyclass+" => "+method_.classpar+"."+method_.name+" : "+method_.offset);
            }
        }
    }

}
