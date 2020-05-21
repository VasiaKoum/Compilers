import visitor.GJDepthFirst;
import syntaxtree.*;
import java.util.*;
import java.io.*;

public class GenCode extends GJDepthFirst<String, String>{
    SymbolTable symboltable;
    String register, currtype;
    LinkedHashMap<String, HashMap<String, Methods>> vtable;
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
        String reg2 = n.f2.accept(this, " ");
        storevar = true;
        String reg1 = n.f0.accept(this, " ");
        storevar = false;
        String type = currtype;
        write_to_ll("\tstore "+currtype+" "+reg2+", "+currtype+"* "+reg1+"\n");
        return null;
    }

    public String visit(Identifier n, String argu) {
        String name = n.f0.accept(this, argu);
        Variables idvar;
        String buff="", typevar;
        if(argu!=null){
            if((idvar = symboltable.findvar(symboltable.currentclass.name, symboltable.currentmethod.name, name, true))!=null){
                typevar = typeinbytes(idvar.type);
                Classes findin = symboltable.inclass;
                if(findin!=null) {
                    int var = regnum;
                    int offset = 8+findin.vars.get(idvar.name).offset;
                    buff = buff+"\t%_"+var+" = getelementptr i8, i8* %this, i32 "+offset+"\n";
                    regnum+=1;
                    buff = buff+"\t%_"+regnum+" = bitcast i8* %_"+var+" to "+typeinbytes(idvar.type)+"*\n";
                    write_to_ll(buff);
                    typevar = typeinbytes(idvar.type);
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
                else currtype = typeinbytes(idvar.type);
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

    public String visit(AllocationExpression n, String argu) {
        String name = n.f1.accept(this, argu), regret=null, buff;
        HashMap<String, Methods> methodmap = vtable.get(name);
        int regcast = regnum, regcall = regnum;
        if(methodmap!=null){
            int sizeOffset = symboltable.sizeClass(name)+8;
            buff = "\t%_"+regnum+" = call i8* @calloc(i32 1, i32 "+sizeOffset+")\n";
            regnum+=1;
            buff = buff+"\t%_"+regnum+" = bitcast i8* %_"+regcall+" to i8***\n";
            regcast=regnum; regnum+=1;
            buff = buff+"\t%_"+regnum+" = getelementptr ["+vtable.get(name).size()+" x i8*], ["+vtable.get(name).size()+" x i8*]* @."+name+"_vtable, i32 0, i32 0\n"+
            "\tstore i8** %_"+regnum+", i8*** %_"+regcast+"\n";
            regret = "%_"+regcall;
            regnum+=1;
            currtype = "i8*";
            write_to_ll(buff);
        }
        return regret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( ExpressionList() )?
     * f5 -> ")"
     */
    public String visit(MessageSend n, String argu) {
       String _ret=null;
       String reg = n.f0.accept(this, argu);
       n.f2.accept(this, argu);
       n.f4.accept(this, argu);
       System.out.println("MessageSend: "+reg);
       return _ret;
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
