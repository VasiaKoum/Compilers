import visitor.GJDepthFirst;
import syntaxtree.*;
import java.util.*;
import java.io.*;

public class TypeChecking extends GJDepthFirst<String, LinkedHashMap<String, Variables>>{
    SymbolTable symboltable, STsymboltable;

    public TypeChecking(SymbolTable st, SymbolTable finalst){
        this.STsymboltable = st;
        this.symboltable = finalst;
    }

    public String visit(MainClass n, LinkedHashMap<String, Variables> argu) {
       String _ret=null;
       String nameclass = n.f1.accept(this, argu);
       Classes addClass = new Classes(nameclass, null);
       Methods addMethod = new Methods("main", nameclass, "void");
       symboltable.classes.put(nameclass, addClass);
       symboltable.methods.put(nameclass+"main", addMethod);
       symboltable.currentclass = addClass;
       symboltable.currentmethod = addMethod;
       symboltable.classmethod = true;
       n.f14.accept(this, symboltable.methods.get(nameclass+"main").vars);
       n.f15.accept(this, symboltable.methods.get(nameclass+"main").vars);
       return _ret;
    }

    public String visit(NodeToken n, LinkedHashMap<String, Variables> argu) { return n.toString(); }

    public String visit(VarDeclaration n, LinkedHashMap<String, Variables> argu) {
      String _ret=null;
      String type = n.f0.accept(this, argu);
      String name = n.f1.accept(this, argu);
      System.out.println("\t"+type+" "+name);
      argu.put(name, new Variables(name, type));
      return _ret;
    }

    public String visit(BooleanArrayType n, LinkedHashMap<String, Variables> argu) {
      return "boolean[]";
    }

    public String visit(IntegerArrayType n, LinkedHashMap<String, Variables> argu) {
      return "int[]";
    }

    public String visit(ClassDeclaration n, LinkedHashMap<String, Variables> argu) {
      String _ret=null;
      String name = n.f1.accept(this, argu);
      System.out.println(name+":");
      Classes addClass = new Classes(name, null);
      symboltable.classes.put(name, addClass);
      symboltable.currentclass = addClass;
      symboltable.classmethod = true;
      n.f3.accept(this, symboltable.classes.get(name).vars);
      n.f4.accept(this, argu);
      System.out.println("--------------------------------");
      return _ret;
    }

    public String visit(ClassExtendsDeclaration n, LinkedHashMap<String, Variables> argu) {
        String _ret=null;
        String name, parentname;
        name = n.f1.accept(this, argu);
        parentname = n.f3.accept(this, argu);
        System.out.println(parentname+":"+name+":");
        Classes addClass = new Classes(name, parentname);
        symboltable.classes.put(name, addClass);
        symboltable.currentclass = addClass;
        symboltable.classmethod = true;
        // List<Long> parentvars = new ArrayList<>(STsymboltable.classes.get(parentname).vars.keySet());
        // List<Long> vars = new ArrayList<>(STsymboltable.classes.get(name).vars.keySet());
        // if(parentvars.containsAll(vars)) System.out.println("Double declaration");
        // else{
        //     n.f5.accept(this, symboltable.classes.get(name).vars);
        //     n.f6.accept(this, argu);
        System.out.println("--------------------------------");
        return _ret;
    }

    public String visit(FormalParameter n, LinkedHashMap<String, Variables> argu) {
        String _ret=null;
        String name = n.f1.accept(this, argu);
        String type = n.f0.accept(this, argu);
        System.out.println("\t["+type+" "+name+"]");
        argu.put(name, new Variables(name, type));
        return _ret;
    }

    public String visit(MethodDeclaration n, LinkedHashMap<String, Variables> argu) {
        String _ret=null;
        String name = n.f2.accept(this, argu);
        String type = n.f1.accept(this, argu);
        String checkmethod = symboltable.currentclass.name+name;
        System.out.println(name+":");
        Methods addMethod = new Methods(name, symboltable.currentclass.name, type);
        symboltable.currentmethod = addMethod;
        symboltable.methods.put(checkmethod, addMethod);
        symboltable.classmethod = false;
        n.f4.accept(this, symboltable.methods.get(checkmethod).args);
        n.f7.accept(this, symboltable.methods.get(checkmethod).vars);
        n.f8.accept(this, symboltable.methods.get(checkmethod).vars);
        return _ret;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "="
     * f2 -> Expression()
     * f3 -> ";"
     */
    public String visit(AssignmentStatement n,  LinkedHashMap<String, Variables> argu) {
        String _ret=null;
        String name = n.f0.accept(this, argu);
        String typeid = symboltable.currenttypevar;
        String expr = n.f2.accept(this, argu);
        String typeexpr = symboltable.currenttypevar;
        Variables idvar, exprvar;

        if((idvar = symboltable.findvar(symboltable.currentclass.name, symboltable.currentmethod.name, name))!=null){
            System.out.println("Exists!");
            if(typeexpr == "int"){
                if(idvar.type == "int") System.out.println("Accepted: "+name+" = "+expr);
                else{
                    System.out.println("Incompatible types: int cannot be converted to "+idvar.type);
                    System.exit(0);
                }
            }
            else if(typeexpr == "boolean"){
                if(idvar.type == "boolean") System.out.println("Accepted: "+name+" = "+expr);
                else{
                    System.out.println("Incompatible types: int cannot be converted to "+idvar.type);
                    System.exit(0);
                }
            }
            else{
                // FIX THAT!
                if((exprvar = symboltable.findvar(symboltable.currentclass.name, symboltable.currentmethod.name, expr))!=null){
                    if(idvar.type == exprvar.type) {
                        System.out.println("Accepted: "+name+" = "+expr);
                    }
                    else{
                        System.out.println("Incompatible types: "+exprvar.type+" cannot be converted to "+idvar.type);
                        System.exit(0);
                    }
                }
                else{
                    System.out.println("Identifier "+expr+ " in expression is not declared!");
                    System.exit(0);
                }
            }
        }
        else{
            System.out.println("Identifier "+name+ " in expression is not declared!");
            System.exit(0);
        }
        return _ret;
    }

    public String visit(Identifier n, LinkedHashMap<String, Variables> argu) {
        String name = n.f0.accept(this, argu);
        symboltable.currenttypevar = name;
        return name;
    }

    public String visit(IntegerLiteral n, LinkedHashMap<String, Variables> argu) {
        symboltable.currenttypevar = "int";
        return n.f0.accept(this, argu);
    }

    public String visit(TrueLiteral n, LinkedHashMap<String, Variables> argu) {
        symboltable.currenttypevar = "boolean";
        return n.f0.accept(this, argu);
    }

    public String visit(FalseLiteral n, LinkedHashMap<String, Variables> argu) {
        symboltable.currenttypevar = "boolean";
        return n.f0.accept(this, argu);
    }

    public void IterateHashMap(){
        System.out.println("\n\nLINKEDHASHMAP printing: ");
        for (String keyclass : symboltable.classes.keySet()) {
            System.out.println(symboltable.classes.get(keyclass).parent+" : "+symboltable.classes.get(keyclass).name);
            for (String keyvars : symboltable.classes.get(keyclass).vars.keySet()) {
                System.out.println("\t"+symboltable.classes.get(keyclass).vars.get(keyvars).type+" "+symboltable.classes.get(keyclass).vars.get(keyvars).name);
            }
        }

        for (String keymethod : symboltable.methods.keySet()) {
            System.out.println(symboltable.methods.get(keymethod).type+" "+symboltable.methods.get(keymethod).name);
            for (String keyvars : symboltable.methods.get(keymethod).args.keySet()) {
                System.out.println("\t["+symboltable.methods.get(keymethod).args.get(keyvars).type+" "+symboltable.methods.get(keymethod).args.get(keyvars).name+"]");
            }
            for (String keyvars : symboltable.methods.get(keymethod).vars.keySet()) {
                System.out.println("\t"+symboltable.methods.get(keymethod).vars.get(keyvars).type+" "+symboltable.methods.get(keymethod).vars.get(keyvars).name);
            }
        }
    }
}
