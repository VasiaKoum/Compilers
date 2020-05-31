import visitor.GJDepthFirst;
import syntaxtree.*;
import java.util.*;
import java.io.*;

public class GenCode extends GJDepthFirst<String, String>{
    SymbolTable symboltable;
    String register, currtype, exprlist;
    LinkedHashMap<String, HashMap<String, Methods>> vtable;
    Variables var;
    FileWriter llfile;
    Boolean storevar;
    int regnum, ifnum, whilenum, andnum, indexnum;

    // clang -o out1 ex.ll

    public GenCode(SymbolTable st, FileWriter file){
        this.symboltable = st;
        this.llfile = file;
        this.regnum = 0;    this.ifnum = 0; this.whilenum = 0;
        this.register = ""; this.currtype = ""; this.exprlist = "";
        this.storevar = false;
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
        symboltable.scope = "Vars";
        n.f14.accept(this, argu);
        n.f15.accept(this, argu);
        write_to_ll("\n\tret i32 0\n}\n");
        return null;
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

    public String visit(VarDeclaration n, String argu) {
        String buff;
        String name = n.f1.accept(this, argu);
        if(!"Class".equals(symboltable.scope)){
            buff = "\t%"+name+" = alloca "+typeinbytes(n.f0.accept(this, argu))+"\n";
            write_to_ll(buff);
        }
        return name;
    }

    public String visit(MethodDeclaration n, String argu) {
        String name = n.f2.accept(this, argu);
        String type = n.f1.accept(this, argu);
        regnum = 0;
        write_to_ll("\ndefine "+typeinbytes(type)+" @"+symboltable.currentclass.name+"."+name+"(i8* %this");
        symboltable.currentmethod = new Methods(name, symboltable.currentclass.name, type);
        n.f4.accept(this, argu);
        write_to_ll(") {\n");
        allocationvars();
        symboltable.scope = "Args";
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

    public String visit(BooleanArrayType n, String argu) {
        return n.f0.accept(this, argu)+"[]";
    }

    public String visit(IntegerArrayType n, String argu) {
        return n.f0.accept(this, argu)+"[]";
    }

    // ----------------S T A T E M E N T S----------------

    public String visit(AssignmentStatement n, String argu) {
        storevar = false;
        String reg2 = n.f2.accept(this, "var"), type1=null;
        storevar = true;
        String reg1 = n.f0.accept(this, "var");
        storevar = false;
        String type = typeinbytes(currtype);
        write_to_ll("\tstore "+type+" "+reg2+", "+type+"* "+reg1+"\n");
        return null;
    }

    public String visit(ArrayAssignmentStatement n, String argu) {
        indexnum+=1;
        String indexok="index_ok_"+(indexnum-1);
        String indexerror="index_error_"+(indexnum-1);
        String reg = n.f0.accept(this, "array"), buff, tmpreg;
        String type = typeinbytes(currtype.substring(0, currtype.length()-2));
        tmpreg=reg;
        Boolean boolarray=false;
        if(currtype.equals("boolean[]")){
            write_to_ll("\t%_"+regnum+" = bitcast i8* "+reg+" to i32*\n");
            tmpreg="%_"+regnum;
            regnum+=1;
            boolarray=true;
        }
        write_to_ll("\t%_"+regnum+" = load i32, i32* "+tmpreg+"\n");
        int regtmp = regnum; regnum+=1;
        String expr1 = n.f2.accept(this, argu);
        if(boolarray){
            buff="\t%_"+regnum+" = icmp sge i32 "+expr1+", "+0+"\n"+
            "\t%_"+(regnum+1)+" = icmp slt i32 "+expr1+", %_"+regtmp+"\n"+
            "\t%_"+(regnum+2)+" = and i1 %_"+regnum+", %_"+(regnum+1)+"\n"+
            "\tbr i1 %_"+(regnum+2)+", label %"+indexok+", label %"+indexerror+"\n"+
            "\n"+indexerror+":\n"+"\tcall void @throw_oob()\n\tbr label %"+indexok+"\n"+
            "\n"+indexok+":\n";
            write_to_ll(buff);
            regnum+=3;
            String expr2 = n.f5.accept(this, argu);
            buff="\t%_"+regnum+" = add i32 4, "+expr1+"\n"+
            "\t%_"+(regnum+1)+" = zext i1 "+expr2+" to i8\n"+
            "\t%_"+(regnum+2)+" = getelementptr i8, i8* "+reg+", i32 %_"+regnum+"\n"+
            "\tstore i8 %_"+(regnum+1)+", i8* %_"+(regnum+2)+"\n";
            write_to_ll(buff);
            regnum+=3;
        }
        else{
            buff="\t%_"+regnum+" = icmp sge i32 "+expr1+", "+0+"\n"+
            "\t%_"+(regnum+1)+" = icmp slt i32 "+expr1+", %_"+regtmp+"\n"+
            "\t%_"+(regnum+2)+" = and i1 %_"+regnum+", %_"+(regnum+1)+"\n"+
            "\tbr i1 %_"+(regnum+2)+", label %"+indexok+", label %"+indexerror+"\n"+
            "\n"+indexerror+":\n"+"\tcall void @throw_oob()\n\tbr label %"+indexok+"\n"+
            "\n"+indexok+":\n";
            write_to_ll(buff);
            regnum+=3;
            String expr2 = n.f5.accept(this, argu);
            buff="\t%_"+regnum+" = add i32 1, "+expr1+"\n"+
            "\t%_"+(regnum+1)+" = getelementptr "+type+", "+type+"* "+reg+", i32 %_"+regnum+"\n"+
            "\tstore "+type+" "+expr2+", "+type+"* %_"+(regnum+1)+"\n";
            write_to_ll(buff);
            regnum+=2;
        }
        return null;
    }

    public String visit(IfStatement n, String argu) {
        ifnum+=1;
        String iflabel="if_"+(ifnum-1);
        String elselabel="else_"+(ifnum-1);
        String endlabel="fi_"+(ifnum-1);
        String expr=n.f2.accept(this, "var");

        write_to_ll("\tbr i1 "+expr+", label %"+iflabel+", label %"+elselabel+"\n\n"+iflabel+":\n");
        n.f4.accept(this, "var");
        write_to_ll("\tbr label %"+endlabel+"\n\n"+elselabel+":\n");
        n.f6.accept(this, "var");
        write_to_ll("\tbr label %"+endlabel+"\n\n"+endlabel+":\n");
        return null;
    }

    public String visit(WhileStatement n, String argu) {
        whilenum+=1;
        String whilelabel="while_"+(whilenum-1);
        String dolabel="do_"+(whilenum-1);
        String donelabel="done_"+(whilenum-1);

        write_to_ll("\tbr label %"+whilelabel+"\n\n"+whilelabel+":\n");
        String expr=n.f2.accept(this, "expr");
        write_to_ll("\tbr i1 "+expr+", label %"+dolabel+", label %"+donelabel+"\n\n"+dolabel+":\n");
        n.f4.accept(this, "expr");
        write_to_ll("\tbr label %"+whilelabel+"\n\n"+donelabel+":\n");
        return null;
    }

    public String visit(PrintStatement n, String argu) {
        String _ret=null;
        String regexpr = n.f2.accept(this, " ");
        write_to_ll("\tcall void (i32) @print_int(i32 "+regexpr+")\n");
        return null;
    }

    // ----------------E X P R E S S I O N S----------------

    public String visit(Expression n, String argu) {
        String returned = n.f0.accept(this, argu);
        write_to_ll("\n");
        return returned;
    }

    public String visit(AndExpression n, String argu) {
        andnum+=1;
        String ret=null, reg1, reg2;
        String andfalse="and_false_"+(andnum-1);
        String andright="and_right_"+(andnum-1);
        String andtmp="and_tmp_"+(andnum-1);
        String andexit="and_exit_"+(andnum-1);

        reg1=n.f0.accept(this, "boolean");
        write_to_ll("\tbr i1 "+reg1+", label %"+andright+", label %"+andfalse+
        "\n\n"+andfalse+":\n\tbr label %"+andtmp+"\n\n"+andright+":\n");
        reg2=n.f2.accept(this, "boolean");
        write_to_ll("\tbr label %"+andtmp+"\n\n"+andtmp+":\n\tbr label %"+andexit+
        "\n\n"+andexit+":\n"+"\t%_"+regnum+" = phi i1 [ 0, %"+andfalse+" ], [ "+reg2+", %"+andtmp+" ]\n");
        ret="%_"+regnum; regnum+=1;
        // currtype="boolean";
        return ret;
    }

    public String visit(CompareExpression n, String argu) {
        String reg1 = n.f0.accept(this, "int");
        String reg2 = n.f2.accept(this, "int");
        String ret = "%_"+regnum;
        write_to_ll( "\t%_"+regnum+" = icmp slt i32 "+reg1+", "+reg2+"\n");
        regnum+=1;
        // currtype="boolean";
        return ret;
    }

    public String visit(PlusExpression n, String argu) {
        String reg1 = n.f0.accept(this, "int");
        String reg2 = n.f2.accept(this, "int");
        String ret = "%_"+regnum;
        write_to_ll( "\t%_"+regnum+" = add i32 "+reg1+", "+reg2+"\n");
        regnum+=1;
        // currtype="int";
        return ret;
    }

    public String visit(MinusExpression n, String argu) {
        String reg1 = n.f0.accept(this, "int");
        String reg2 = n.f2.accept(this, "int");
        String ret = "%_"+regnum;
        write_to_ll( "\t%_"+regnum+" = sub i32 "+reg1+", "+reg2+"\n");
        regnum+=1;
        // currtype="int";
        return ret;
    }

    public String visit(TimesExpression n, String argu) {
        String reg1 = n.f0.accept(this, "int");
        String reg2 = n.f2.accept(this, "int");
        String ret = "%_"+regnum;
        write_to_ll( "\t%_"+regnum+" = mul i32 "+reg1+", "+reg2+"\n");
        regnum+=1;
        // currtype="int";
        return ret;
    }

    public String visit(ArrayLookup n, String argu) {
        String ret;
        indexnum+=1;
        String indexok="index_ok_"+(indexnum-1);
        String indexerror="index_error_"+(indexnum-1);
        String reg = n.f0.accept(this, "array"), buff;
        String typearray = currtype;

        String type = typeinbytes(currtype.substring(0, currtype.length()-2));
        if(typearray.equals("boolean[]")){
            buff="\t%_"+regnum+" = bitcast i8* "+reg+" to i32*\n"+
            "\t%_"+(regnum+1)+" = load i32, i32* %_"+regnum+"\n";
            write_to_ll(buff);
            int regtmp = (regnum+1); regnum+=2;
            String expr1 = n.f2.accept(this, argu);
            buff="\t%_"+regnum+" = icmp sge i32 "+expr1+", "+0+"\n"+
            "\t%_"+(regnum+1)+" = icmp slt i32 "+expr1+", %_"+regtmp+"\n"+
            "\t%_"+(regnum+2)+" = and i1 %_"+regnum+", %_"+(regnum+1)+"\n"+
            "\tbr i1 %_"+(regnum+2)+", label %"+indexok+", label %"+indexerror+"\n"+
            "\n"+indexerror+":\n"+"\tcall void @throw_oob()\n\tbr label %"+indexok+"\n"+
            "\n"+indexok+":\n"+"\t%_"+(regnum+3)+" = add i32 4, "+expr1+"\n"+
            "\t%_"+(regnum+4)+" = getelementptr i8, i8* "+reg+", i32 %_"+(regnum+3)+"\n"+
            "\t%_"+(regnum+5)+" = load i8, i8* %_"+(regnum+4)+"\n"+
            "\t%_"+(regnum+6)+" = trunc i8 %_"+(regnum+5)+" to i1\n";
            write_to_ll(buff);
            ret = "%_"+(regnum+6); regnum+=7;
        }
        else{
            write_to_ll("\t%_"+regnum+" = load "+type+", "+type+"* "+reg+"\n");
            int regtmp = regnum; regnum+=1;
            String expr1 = n.f2.accept(this, argu);
            buff="\t%_"+regnum+" = icmp sge i32 "+expr1+", "+0+"\n"+
            "\t%_"+(regnum+1)+" = icmp slt i32 "+expr1+", %_"+regtmp+"\n"+
            "\t%_"+(regnum+2)+" = and i1 %_"+regnum+", %_"+(regnum+1)+"\n"+
            "\tbr i1 %_"+(regnum+2)+", label %"+indexok+", label %"+indexerror+"\n"+
            "\n"+indexerror+":\n"+"\tcall void @throw_oob()\n\tbr label %"+indexok+"\n"+
            "\n"+indexok+":\n"+"\t%_"+(regnum+3)+" = add i32 1, "+expr1+"\n"+
            "\t%_"+(regnum+4)+" = getelementptr "+type+", "+type+"* "+reg+", i32 %_"+(regnum+3)+"\n"+
            "\t%_"+(regnum+5)+" = load "+type+", "+type+"* %_"+(regnum+4)+"\n";
            write_to_ll(buff);
            ret = "%_"+(regnum+5); regnum+=6;
        }
        return ret;
    }

    public String visit(ArrayLength n, String argu) {
        String ret;
        String reg = n.f0.accept(this, "var");
        System.out.println("ArrayLength: "+currtype);
        if(currtype.equals("boolean[]")){
            write_to_ll("\t%_"+regnum+" = bitcast i8* "+reg+" to i32*\n"+"\t%_"+(regnum+1)+" = load i32, i32* %_"+regnum+"\n");
            ret="%_"+(regnum+1); regnum+=2;
        }
        else{
            write_to_ll("\t%_"+regnum+" = load i32, i32* "+reg+"\n");
            ret="%_"+regnum; regnum+=1;
        }

        // currtype = "int";
        return ret;
    }

    public String visit(MessageSend n, String argu) {
        String reg = n.f0.accept(this, "var");
        Methods method;
        int register;
        exprlist = "";
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
        n.f4.accept(this, argu);

        String[] args_array = (reg+","+exprlist).split(",");
        String[] types_array = argsinbytes(method).split(",");
        String printargs = argswithtypes(types_array, args_array);

        buff = "\t%_"+regnum+" = call "+typeinbytes(method.type)+" %_"+register+"("+printargs+")\n";
        register = regnum; regnum+=1;
        write_to_ll(buff);
        currtype=method.type;
        return "%_"+register;
    }

    public String visit(ExpressionList n, String argu) {
        exprlist=n.f0.accept(this, "var");
        n.f1.accept(this, argu);
        return null;
    }

    public String visit(ExpressionTerm n, String argu) {
        exprlist = exprlist+","+n.f1.accept(this, "var");
        return null;
    }

    public String visit(IntegerLiteral n, String argu) {
        currtype = "int";
       return n.f0.accept(this, argu);
    }

    public String visit(TrueLiteral n, String argu) {
        currtype = "boolean";
        return "1";
    }

    public String visit(FalseLiteral n, String argu) {
        currtype = "boolean";
        return "0";
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

    public String visit(ThisExpression n, String argu) {
        currtype = symboltable.currentclass.name;
        return "%this";
    }

    public String visit(BooleanArrayAllocationExpression n, String argu) {
        indexnum+=1;
        String indexok="index_ok_"+(indexnum-1);
        String indexerror="index_error_"+(indexnum-1);
        String buff, ret;

        String reg = n.f3.accept(this, argu);
        buff="\t%_"+regnum+" = add i32 4"+", "+reg+"\n"+
        "\t%_"+(regnum+1)+" = icmp sge i32 %_"+regnum+", 4\n"+
        "\tbr i1 %_"+(regnum+1)+", label %"+indexok+", label %"+indexerror+"\n\n"+
        indexerror+":\n\tcall void @throw_nsz()\n\tbr label %"+indexok+"\n\n"+
        indexok+":\n\t%_"+(regnum+2)+" = call i8* @calloc(i32 1, i32 %_"+regnum+")\n"+
        "\t%_"+(regnum+3)+" = bitcast i8* %_"+(regnum+2)+" to i32*\n"+
        "\tstore i32 "+reg+", i32* %_"+(regnum+3);
        ret="%_"+(regnum+2); regnum+=4;
        write_to_ll(buff);
        currtype="boolean[]";
        return ret;
    }

    public String visit(IntegerArrayAllocationExpression n, String argu) {
        indexnum+=1;
        String indexok="index_ok_"+(indexnum-1);
        String indexerror="index_error_"+(indexnum-1);
        String buff, ret;

        String reg = n.f3.accept(this, argu);
        buff="\t%_"+regnum+" = add i32 1"+", "+reg+"\n"+
        "\t%_"+(regnum+1)+" = icmp sge i32 %_"+regnum+", 1\n"+
        "\tbr i1 %_"+(regnum+1)+", label %"+indexok+", label %"+indexerror+"\n\n"+
        indexerror+":\n\tcall void @throw_nsz()\n\tbr label %"+indexok+"\n\n"+
        indexok+":\n\t%_"+(regnum+2)+" = call i8* @calloc(i32 %_"+regnum+", i32 4)\n"+
        "\t%_"+(regnum+3)+" = bitcast i8* %_"+(regnum+2)+" to i32*\n"+
        "\tstore i32 "+reg+", i32* %_"+(regnum+3)+"\n";
        ret="%_"+(regnum+3); regnum+=4;
        write_to_ll(buff);
        currtype="int[]";
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

    public String visit(NotExpression n, String argu) {
        String reg=n.f1.accept(this, "boolean");
        String ret = "%_"+regnum;
        write_to_ll( "\t%_"+regnum+" = xor i1 1, "+reg+"\n");
        regnum+=1;
        // currtype="boolean";
        return ret;
    }

    public String visit(BracketExpression n, String argu) {
        return n.f1.accept(this, argu);
    }

    // ----------------F U N C T I O N S----------------

    public String typeinbytes(String type){
        String returned="";
        if("int".equals(type)) returned = "i32";
        else if("boolean".equals(type)) returned = "i1";
        else if("int[]".equals(type)) returned = "i32*";
        else returned = "i8*";
        return returned;
    }

    public String argswithtypes(String[] types, String[] args){
        String returned="";
        if(types.length==args.length){
            for(int i=0; i<types.length; i++){
                returned = returned+types[i]+" "+args[i]+",";
            }
        }
        if(returned.length()>0) returned=returned.substring(0, returned.length()-1);
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
