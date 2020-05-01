import visitor.GJDepthFirst;
import syntaxtree.*;
import java.util.*;
import java.io.*;

public class TypeChecking extends GJDepthFirst<String, String>{
    SymbolTable symboltable, STsymboltable;

    public TypeChecking(SymbolTable st, SymbolTable finalst){
        this.STsymboltable = st;
        this.symboltable = finalst;
    }

    public String visit(MainClass n, String argu) {
       String _ret=null;
       String nameclass = n.f1.accept(this, argu);
       Classes addClass = new Classes(nameclass, null);
       Methods addMethod = new Methods("main", nameclass, "void");
       symboltable.classes.put(nameclass, addClass);
       symboltable.methods.put(nameclass+"main", addMethod);
       symboltable.currentclass = addClass;
       symboltable.currentmethod = addMethod;
       symboltable.scope = "Vars";
       n.f14.accept(this, argu);
       n.f15.accept(this, argu);
       return _ret;
    }

    public String visit(NodeToken n, String argu) { return n.toString(); }

    public String visit(VarDeclaration n, String argu) {
      String _ret=null;
      String type = n.f0.accept(this, argu);
      String name = n.f1.accept(this, argu);
      System.out.println("\t"+type+" "+name);
      symboltable.putvar(STsymboltable, symboltable.currentclass.name, symboltable.currentmethod.name, new Variables(name, type), symboltable.scope);
      return _ret;
    }

    public String visit(BooleanArrayType n, String argu) {
      return "boolean[]";
    }

    public String visit(IntegerArrayType n, String argu) {
      return "int[]";
    }

    public String visit(ClassDeclaration n, String argu) {
      String _ret=null;
      String name = n.f1.accept(this, argu);
      System.out.println(name+":");
      Classes addClass = new Classes(name, null);
      symboltable.classes.put(name, addClass);
      symboltable.currentclass = addClass;
      symboltable.scope = "Class";
      n.f3.accept(this, argu);
      n.f4.accept(this, argu);
      System.out.println("--------------------------------");
      return _ret;
    }

    public String visit(ClassExtendsDeclaration n, String argu) {
        String _ret=null;
        String name, parentname;
        name = n.f1.accept(this, argu);
        parentname = n.f3.accept(this, argu);
        System.out.println(parentname+":"+name+":");
        Classes addClass = new Classes(name, parentname);
        symboltable.classes.put(name, addClass);
        symboltable.currentclass = addClass;
        symboltable.scope = "Class";
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        System.out.println("--------------------------------");
        return _ret;
    }

    public String visit(FormalParameter n, String argu) {
        String _ret=null;
        String name = n.f1.accept(this, argu);
        String type = n.f0.accept(this, argu);
        System.out.println("\t["+type+" "+name+"]");
        symboltable.scope = "Args";
        symboltable.putvar(STsymboltable, symboltable.currentclass.name, symboltable.currentmethod.name, new Variables(name, type), symboltable.scope);
        return _ret;
    }

    public String visit(MethodDeclaration n, String argu) {
        String _ret=null;
        String name = n.f2.accept(this, argu);
        String type = n.f1.accept(this, argu);
        String checkmethod = symboltable.currentclass.name+name;
        System.out.println(name+":");
        Methods addMethod = new Methods(name, symboltable.currentclass.name, type);
        symboltable.currentmethod = addMethod;
        symboltable.methods.put(checkmethod, addMethod);
        symboltable.scope = "Args";
        n.f4.accept(this, argu);
        symboltable.scope = "Vars";
        n.f7.accept(this, argu);
        n.f8.accept(this, argu);
        return _ret;
    }

    public String visit(AssignmentStatement n,  String argu) {
        String _ret=null;
        String name = n.f0.accept(this, argu);
        String expr;
        Variables idvar;
        if((idvar = symboltable.findvar(symboltable.currentclass.name, symboltable.currentmethod.name, name))!=null){
            expr = n.f2.accept(this, idvar.type);
            if(!(idvar.type.equals(expr))){
                System.out.println("AssignmentStatement: Expression is type of: "+expr+ ", expected "+idvar.type+".");
                System.exit(0);
            }
        }
        else{
            System.out.println("AssignmentStatement: Identifier "+name+ " in expression is not declared!");
            System.exit(0);
        }
        return _ret;
    }

    public String visit(AndExpression n, String argu) {
       String _ret=null;
       if(argu.equals("boolean")){
           n.f0.accept(this, "boolean");
           n.f2.accept(this, "boolean");
       }
       else{
           System.out.println("AndExpression: Expression is type of: "+argu+ ", expected boolean.");
           System.exit(0);
       }
       return "boolean";
    }

    public String visit(CompareExpression n, String argu) {
       String _ret=null;
       if(argu.equals("boolean")){
           n.f0.accept(this, "int");
           n.f2.accept(this, "int");
       }
       else{
           System.out.println("CompareExpression: Expression is type of: "+argu+ ", expected boolean.");
           System.exit(0);
       }
       return "boolean";
    }

    public String visit(PlusExpression n, String argu) {
       String _ret=null;
       if(argu.equals("int")){
           n.f0.accept(this, "int");
           n.f2.accept(this, "int");
       }
       else{
           System.out.println("PlusExpression: Expression is type of: "+argu+ ", expected int.");
           System.exit(0);
       }
       return "int";
    }

    public String visit(MinusExpression n, String argu) {
       String _ret=null;
       if(argu.equals("int")){
           n.f0.accept(this, "int");
           n.f2.accept(this, "int");
       }
       else{
           System.out.println("MinusExpression: Expression is type of: "+argu+ ", expected int.");
           System.exit(0);
       }
       return "int";
    }

    public String visit(TimesExpression n, String argu) {
       String _ret=null;
       if (argu.equals("int")){
           n.f0.accept(this, "int");
           n.f2.accept(this, "int");
       }
       else{
           System.out.println("TimesExpression: Expression is type of: "+argu+ ", expected int.");
           System.exit(0);
       }
       return "int";
    }

    public String visit(ArrayLookup n, String argu) {
       String _ret=null;
       String type = n.f0.accept(this, "[]");
       Variables var;
       String index = n.f2.accept(this, "int");
       if(index.equals("int")){
           if(type.equals("int[]") || type.equals("boolean[]")){
               type = type.replace("[]","");
           }
           else{
               System.out.println("ArrayLookup: Expression is type of: "+type+ ", expected arraytype.");
               System.exit(0);
           }
       }
       else{
           System.out.println("ArrayLookup: Index in arraytype is type of: "+index+ ", expected int.");
           System.exit(0);
       }
       return type;
    }

    public String visit(ArrayLength n, String argu) {
       String _ret=null;
       String type = n.f0.accept(this, "[]");
       if(!(type.equals("int[]") || type.equals("boolean[]"))){
           System.out.println("ArrayLength: Expression is type of: "+type+ ", expected arraytype.");
           System.exit(0);
       }
       return "int";
    }

    public String visit(MessageSend n, String argu) {
       String _ret=null;
       String prim = n.f0.accept(this, null);
       String id = n.f2.accept(this, null);
       Methods method = null;
       String args = null;
       if(argu.equals("boolean") || argu.equals("int")){
           if((method = symboltable.findmethod(symboltable.currentclass.name, symboltable.currentmethod.name, prim, id))!=null){
               for (String keyvars : method.args.keySet()) {
                   if(args==null) args = method.args.get(keyvars).type;
                   else args = args +","+method.args.get(keyvars).type;
               }
               symboltable.methodpars = args;
               symboltable.numpars = 0;
               _ret = method.type;
               n.f4.accept(this, argu);
               if(method.args.size()!=symboltable.numpars){
                    System.out.println("MessageSend: Method "+method.name+" cannot be applied to given types.");
                    System.exit(0);
               }
           }
           else{
               System.out.println("MessageSend: Not method found: "+id+".");
               System.exit(0);
           }
       }
       else{
           System.out.println("MessageSend: Expression is type of: "+argu+ ", expected int or boolean.");
           System.exit(0);
       }
       return _ret;
    }

    public String visit(ExpressionList n, String argu) {
        String _ret=null;
        String args[];
        args = symboltable.methodpars.split(",", 2);
        if(args.length>1) symboltable.methodpars = args[1];
        else args[0] = symboltable.methodpars;
        String expr = n.f0.accept(this, args[0]);
        if(!args[0].equals(expr)){
            System.out.println("ExpressionList: Wrong given types at method, given: "+expr+" expected: "+args[0]);
            System.exit(0);
        }
        String exprt = n.f1.accept(this, argu);
        symboltable.numpars+=1;
        return _ret;
    }

    public String visit(ExpressionTerm n, String argu) {
       String _ret=null;
       String args[];
       args = symboltable.methodpars.split(",", 2);
       if(args.length>1) symboltable.methodpars = args[1];
       else args[0] = symboltable.methodpars;
       String expr = n.f1.accept(this, args[0]);
       symboltable.numpars+=1;
       if(!args[0].equals(expr)){
           System.out.println("ExpressionTerm: Wrong given types at method, given: "+expr+" expected: "+args[0]);
           System.exit(0);
       }
       return _ret;
    }

    public String visit(Identifier n, String argu) {
        String name = n.f0.accept(this, argu);
        Variables idvar;
        if(argu!=null){
            if((idvar = symboltable.findvar(symboltable.currentclass.name, symboltable.currentmethod.name, name))!=null){
                name = idvar.type;
            }
            else {
                if(!symboltable.isclasstype(STsymboltable, name)){
                    System.out.println("Identifier: Identifier "+name+ " in expression is not declared!");
                    System.exit(0);
                }
            }
        }
        return name;
    }

    public String visit(IntegerLiteral n, String argu) {
        n.f0.accept(this, argu);
        return "int";
    }

    public String visit(TrueLiteral n, String argu) {
        n.f0.accept(this, argu);
        return "boolean";
    }

    public String visit(FalseLiteral n, String argu) {
        n.f0.accept(this, argu);
        return "boolean";
    }

    public String visit(BooleanArrayAllocationExpression n, String argu) {
       String expr = n.f3.accept(this, argu);
       if(!expr.equals("int")){
           System.out.println("BooleanArrayAllocationExpression:  Expression is type of: "+expr+ ", expected int.");
           System.exit(0);
       }
       if(!argu.equals("boolean[]")){
           System.out.println("BooleanArrayAllocationExpression:  Expression is type of: "+argu+ ", expected boolean arraytype.");
           System.exit(0);
       }
       return "boolean[]";
    }

    public String visit(IntegerArrayAllocationExpression n, String argu) {
        String expr = n.f3.accept(this, argu);
        if(!expr.equals("int")){
            System.out.println("IntegerArrayAllocationExpression: Expression is type of: "+expr+ ", expected int.");
            System.exit(0);
        }
        if(!argu.equals("int[]")){
            System.out.println("IntegerArrayAllocationExpression: Expression is type of: "+argu+ ", expected int arraytype.");
            System.exit(0);
        }
       return "int[]";
    }

    public String visit(AllocationExpression n, String argu) {
       String name = n.f1.accept(this, argu);
       if(argu!=null && !argu.equals(name)){
           if(!symboltable.checkparent(STsymboltable, argu, name)){
               System.out.println("AllocationExpression: Expression is type of: "+name+ ", expected "+argu+".");
               System.exit(0);
           }
       }
       return argu;
    }

    public String visit(BracketExpression n, String argu) {
       String expr = n.f1.accept(this, argu);
       if(!argu.equals(expr)){
           System.out.println("BracketExpression: Expression is type of: "+expr+ ", expected "+argu+".");
           System.exit(0);
       }
       return expr;
    }

    public String visit(NotExpression n, String argu) {
       String clause = n.f1.accept(this, "boolean");
       if(!argu.equals("boolean")){
           System.out.println("NotExpression: Expression is type of: "+argu+ ", expected boolean.");
           System.exit(0);
       }
       return "boolean";
    }
}
