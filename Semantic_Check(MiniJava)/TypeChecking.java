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

    public String visit(NodeToken n, LinkedHashMap<String, Variables> argu) { return n.toString(); }

    public String visit(VarDeclaration n, LinkedHashMap<String, Variables> argu) {
      String _ret=null;
      String type = n.f0.accept(this, argu);
      String name = n.f1.accept(this, argu);
      System.out.println("\t"+type+" "+name);
      if (argu == null) System.out.println("current class "+symboltable.currentclass);
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
      symboltable.classes.put(name, new Classes(name, null));
      symboltable.currentclass = name;
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
        symboltable.classes.put(name, new Classes(name, parentname));
        symboltable.currentclass = name;
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
      System.out.println(name+":");
      symboltable.currentmethod = name;
      symboltable.classmethod = false;
      symboltable.methods.put(name, new Methods(name, symboltable.currentclass, type));
      n.f4.accept(this, symboltable.methods.get(name).args);
      n.f7.accept(this, symboltable.methods.get(name).vars);
      n.f8.accept(this, symboltable.methods.get(name).vars);
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
       System.out.println("In class: "+symboltable.currentclass+" In method: "+symboltable.currentmethod);
       System.out.println("Expression: "+name+" = "+expr);
       System.out.println("Expression types: "+typeid+" = "+typeexpr);
       return _ret;
    }

    /**
     * f0 -> <IDENTIFIER>
     */
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
