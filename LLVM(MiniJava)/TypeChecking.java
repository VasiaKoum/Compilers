import visitor.GJDepthFirst;
import syntaxtree.*;
import java.util.*;
import java.io.*;

public class TypeChecking extends GJDepthFirst<String, String>{
    SymbolTable symboltable, STsymboltable;
    String arglist;

    public TypeChecking(SymbolTable st, SymbolTable finalst){
        this.STsymboltable = st;
        this.symboltable = finalst;
        this.arglist = "";
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
      String type = n.f0.accept(this, argu);
      String name = n.f1.accept(this, argu);
      // System.out.println("\t"+type+" "+name);
      symboltable.putvar(STsymboltable, symboltable.currentclass.name, symboltable.currentmethod.name, new Variables(name, type), symboltable.scope);
      return null;
    }

    public String visit(BooleanArrayType n, String argu) {
      return "boolean[]";
    }

    public String visit(IntegerArrayType n, String argu) {
      return "int[]";
    }

    public String visit(ClassDeclaration n, String argu) {
      String name = n.f1.accept(this, argu);
      // System.out.println(name+":");
      Classes addClass = new Classes(name, null);
      symboltable.classes.put(name, addClass);
      symboltable.currentclass = addClass;
      symboltable.scope = "Class";
      n.f3.accept(this, argu);
      n.f4.accept(this, argu);
      // System.out.println("--------------------------------");
      return null;
    }

    public String visit(ClassExtendsDeclaration n, String argu) {
        String name, parentname;
        name = n.f1.accept(this, argu);
        parentname = n.f3.accept(this, argu);
        // System.out.println(parentname+":"+name+":");
        Classes addClass = new Classes(name, parentname);
        symboltable.classes.put(name, addClass);
        symboltable.currentclass = addClass;
        symboltable.scope = "Class";
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        // System.out.println("--------------------------------");
        return null;
    }

    public String visit(FormalParameter n, String argu) {
        String name = n.f1.accept(this, argu);
        String type = n.f0.accept(this, argu);
        // System.out.println("\t["+type+" "+name+"]");
        symboltable.scope = "Args";
        symboltable.putvar(STsymboltable, symboltable.currentclass.name, symboltable.currentmethod.name, new Variables(name, type), symboltable.scope);
        return null;
    }

    public String visit(MethodDeclaration n, String argu) {
        String name = n.f2.accept(this, argu);
        String type = n.f1.accept(this, argu);
        String checkmethod = symboltable.currentclass.name+name;
        Methods addMethod = new Methods(name, symboltable.currentclass.name, type);
        symboltable.currentmethod = addMethod;
        symboltable.methods.put(checkmethod, addMethod);
        symboltable.scope = "Args";
        n.f4.accept(this, argu);
        symboltable.scope = "Vars";
        n.f7.accept(this, argu);
        n.f8.accept(this, argu);
        String returned = n.f10.accept(this, type);
        if(!returned.equals(type))
            if(!symboltable.checkparent(STsymboltable, type, returned))
                throw new RuntimeException("MethodDeclaration: Expression is type of: "+returned+ ", expected "+type+".");
        return null;
    }

    public String visit(AssignmentStatement n,  String argu) {
        String name = n.f0.accept(this, argu);
        String expr;
        Variables idvar;
        if((idvar = symboltable.findvar(symboltable.currentclass.name, symboltable.currentmethod.name, name, false))!=null){
            expr = n.f2.accept(this, idvar.type);
            if(!(idvar.type.equals(expr))){
                if(!symboltable.checkparent(STsymboltable, idvar.type, expr))
                    throw new RuntimeException("AssignmentStatement: Expression is type of: "+expr+ ", expected "+idvar.type+".");
            }
        }
        else throw new RuntimeException("AssignmentStatement: Identifier "+name+ " in expression is not declared!");
        return null;
    }

    public String visit(AndExpression n, String argu) {
        String type1 = n.f0.accept(this, "boolean");
        String type2 = n.f2.accept(this, "boolean");
        if(!type1.equals("boolean"))
            throw new RuntimeException("AndExpression: Expression is type of: "+type1+ ", expected int.");
        if(!type2.equals("boolean"))
            throw new RuntimeException("AndExpression: Expression is type of: "+type2+ ", expected int.");
        return "boolean";
    }

    public String visit(CompareExpression n, String argu) {
        String type1 = n.f0.accept(this, "int");
        String type2 = n.f2.accept(this, "int");
        if(!type1.equals("int"))
            throw new RuntimeException("CompareExpression: Expression is type of: "+type1+ ", expected int.");
        if(!type2.equals("int"))
            throw new RuntimeException("CompareExpression: Expression is type of: "+type2+ ", expected int.");
        return "boolean";
    }

    public String visit(PlusExpression n, String argu) {
        String type1 = n.f0.accept(this, "int");
        String type2 = n.f2.accept(this, "int");
        if(!type1.equals("int"))
            throw new RuntimeException("PlusExpression: Expression is type of: "+type1+ ", expected int.");
        if(!type2.equals("int"))
            throw new RuntimeException("PlusExpression: Expression is type of: "+type2+ ", expected int.");
        return "int";
    }

    public String visit(MinusExpression n, String argu) {
        String type1 = n.f0.accept(this, "int");
        String type2 = n.f2.accept(this, "int");
        if(!type1.equals("int"))
            throw new RuntimeException("MinusExpression: Expression is type of: "+type1+ ", expected int.");
        if(!type2.equals("int"))
            throw new RuntimeException("MinusExpression: Expression is type of: "+type2+ ", expected int.");
        return "int";
    }

    public String visit(TimesExpression n, String argu) {
        String type1 = n.f0.accept(this, "int");
        String type2 = n.f2.accept(this, "int");
        if(!type1.equals("int"))
            throw new RuntimeException("TimesExpression: Expression is type of: "+type1+ ", expected int.");
        if(!type2.equals("int"))
            throw new RuntimeException("TimesExpression: Expression is type of: "+type2+ ", expected int.");
        return "int";
    }

    public String visit(ArrayLookup n, String argu) {
       String type = n.f0.accept(this, "[]");
       Variables var;
       String index = n.f2.accept(this, "int");
       if(index.equals("int")){
           if(type.equals("int[]") || type.equals("boolean[]")){
               type = type.replace("[]","");
           }
           else throw new RuntimeException("ArrayLookup: Expression is type of: "+type+ ", expected arraytype.");
       }
       else throw new RuntimeException("ArrayLookup: Index in arraytype is type of: "+index+ ", expected int.");
       return type;
    }

    public String visit(ArrayLength n, String argu) {
       String type = n.f0.accept(this, "[]");
       if(!(type.equals("int[]") || type.equals("boolean[]"))) throw new RuntimeException("ArrayLength: Expression is type of: "+type+ ", expected arraytype.");
       return "int";
    }

    public String visit(MessageSend n, String argu) {
       String _ret=null;
       String prim = n.f0.accept(this, null);
       String id = n.f2.accept(this, null);
       Methods method = null;
       String args = null;
       arglist = "";
       if((method = symboltable.findmethod(STsymboltable, symboltable.currentclass.name, symboltable.currentmethod.name, prim, id))!=null){
           for (String keyvars : method.args.keySet()) {
               if(args==null) args = method.args.get(keyvars).type;
               else args = args +","+method.args.get(keyvars).type;
           }
           symboltable.methodpars = args;
           _ret = method.type;
           String tmppars = symboltable.methodpars;
           n.f4.accept(this, method.name);
           if(tmppars==null){
               if(!arglist.equals("")) throw new RuntimeException("MessageSend: Method "+method.name+" cannot be applied to given types.");
           }
           else{
                String[] declstrings = tmppars.split(",");
                String[] argustrings = arglist.split(",");
                if(declstrings.length == argustrings.length){
                    for(int i=0; i<declstrings.length; i++){
                        if(!argustrings[i].equals(declstrings[i]))
                            if(!symboltable.checkparent(STsymboltable, declstrings[i], argustrings[i]))
                                throw new RuntimeException("MessageSend: Method "+method.name+" cannot be applied to given types.");
                    }

                }
            }
       }
       else throw new RuntimeException("MessageSend: Not method found: "+id+".");
       return _ret;
    }

    public String visit(ExpressionList n, String argu) {
        arglist=n.f0.accept(this, "var");
        n.f1.accept(this, "var");
        return arglist;
    }

    public String visit(ExpressionTerm n, String argu) {
        arglist=arglist+","+n.f1.accept(this, "var");
        return null;
    }

    public String visit(ArrayAssignmentStatement n, String argu) {
       String _ret=null;
       String name = n.f0.accept(this, argu);
       String expr, index, type;
       Variables idvar;
       if((idvar = symboltable.findvar(symboltable.currentclass.name, symboltable.currentmethod.name, name, false))!=null){
           if(idvar.type.equals("int[]") || idvar.type.equals("boolean[]")){
               type = idvar.type.replace("[]","");
               index = n.f2.accept(this, "int");
               if(index.equals("int")){
                   expr = n.f5.accept(this, type);
                   if(!(type.equals(expr))) throw new RuntimeException("ArrayAssignmentStatement: Expression is type of: "+expr+ ", expected "+type+".");
               }
               else throw new RuntimeException("ArrayAssignmentStatement: the given index is: "+index+ ", expected "+type+".");
           }
           else throw new RuntimeException("ArrayLookup: Expression is type of: "+idvar.type+ ", expected arraytype.");
       }
       else throw new RuntimeException("ArrayAssignmentStatement: Identifier "+name+ " in expression is not declared!");
       return _ret;
    }

    public String visit(IfStatement n, String argu) {
       String expr = n.f2.accept(this, "boolean");
       if(expr.equals("boolean")){
           n.f4.accept(this, argu);
           n.f6.accept(this, argu);
       }
       else throw new RuntimeException("IfStatement: Expression is type of: "+expr+ ", expected boolean");
       return "boolean";
    }

    public String visit(WhileStatement n, String argu) {
       String expr = n.f2.accept(this, "boolean");
       if(expr.equals("boolean")){
           n.f4.accept(this, argu);
       }
       else throw new RuntimeException("IfStatement: Expression is type of: "+expr+ ", expected boolean");
       return null;
    }

    public String visit(PrintStatement n, String argu) {
       String expr = n.f2.accept(this, "int");
       if(!expr.equals("int")) throw new RuntimeException("PrintStatement: Expression is type of: "+expr+ ", expected int.");
       return null;
    }

    public String visit(ThisExpression n, String argu) {
       return symboltable.currentclass.name;
    }

    public String visit(Identifier n, String argu) {
        String name = n.f0.accept(this, argu);
        Variables idvar;
        if(argu!=null){
            if((idvar = symboltable.findvar(symboltable.currentclass.name, symboltable.currentmethod.name, name, false))!=null){
                name = idvar.type;
            }
            else {
                if(!symboltable.isclasstype(STsymboltable, name)) throw new RuntimeException("Identifier: Identifier "+name+ " in expression is not declared!");
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
       String index = n.f3.accept(this, argu);
       if(!index.equals("int")) throw new RuntimeException("BooleanArrayAllocationExpression: Index in arraytype is type of: "+index+ ", expected int.");
       return "boolean[]";
    }

    public String visit(IntegerArrayAllocationExpression n, String argu) {
        String index = n.f3.accept(this, argu);
        if(!index.equals("int")) throw new RuntimeException("IntegerArrayAllocationExpression: Index in arraytype is type of: "+index+ ", expected int.");
       return "int[]";
    }

    public String visit(AllocationExpression n, String argu) {
        String type = n.f1.accept(this, " ");
        return type;
    }

    public String visit(BracketExpression n, String argu) {
       String expr = n.f1.accept(this, argu);
       return expr;
    }

    public String visit(NotExpression n, String argu) {
       String clause = n.f1.accept(this, "boolean");
       if(!clause.equals("boolean")) throw new RuntimeException("NotExpression: Expression is type of: "+argu+ ", expected boolean.");
       return "boolean";
    }
}
