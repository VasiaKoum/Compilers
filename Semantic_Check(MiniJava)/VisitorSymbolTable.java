import visitor.GJDepthFirst;
import syntaxtree.*;
import java.util.*;
import java.io.*;

public class VisitorSymbolTable extends GJDepthFirst<String, String>{
    SymbolTable symboltable;

    public VisitorSymbolTable(SymbolTable st){
        symboltable = st;
    }

    public String visit(MainClass n, String argu) {
       String _ret=null;
       String nameclass = n.f1.accept(this, argu);
       if (symboltable.classes.get("main") != null){
           throw new RuntimeException("MainClass: Already declared: class main.");
       }
       else{
           Classes addClass = new Classes(nameclass, null);
           Methods addMethod = new Methods("main", nameclass, "void");
           symboltable.classes.put(nameclass, addClass);
           symboltable.methods.put(nameclass+"main", addMethod);
           symboltable.currentclass = addClass;
           symboltable.currentmethod = addMethod;
           symboltable.scope = "Vars";
           n.f14.accept(this, argu);
       }
       return _ret;
    }

    public String visit(NodeToken n, String argu) { return n.toString(); }

    public String visit(VarDeclaration n, String argu) {
      String _ret=null;
      String type = n.f0.accept(this, argu);
      String name = n.f1.accept(this, argu);

      symboltable.vardecl(symboltable.currentclass.name, symboltable.currentmethod.name, new Variables(name, type), symboltable.scope);
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
      if (symboltable.classes.get(name) != null){
         throw new RuntimeException("ClassDeclaration: Already declared: class "+name);
      }
      else{
          Classes addClass = new Classes(name, null);
          symboltable.classes.put(name, addClass);
          symboltable.currentclass = addClass;
          symboltable.scope = "Class";
          n.f3.accept(this, argu);
          n.f4.accept(this, argu);
      }
      return _ret;
    }

    public String visit(FormalParameter n, String argu) {
       String _ret=null;
       String name = n.f1.accept(this, argu);
       String type = n.f0.accept(this, argu);
       if(symboltable.findvar(symboltable.currentclass.name, symboltable.currentmethod.name, name, false)!=null){
           throw new RuntimeException("FormalParameter: Already declared: "+type+" "+name);
       }
       else {
           symboltable.putvar(null, symboltable.currentclass.name, symboltable.currentmethod.name, new Variables(name, type), symboltable.scope);
       }
       return _ret;
    }

    public String visit(MethodDeclaration n, String argu){
        String _ret=null;
        String name = n.f2.accept(this, argu);
        String type = n.f1.accept(this, argu);
        String checkmethod = symboltable.currentclass.name+name;
        if (symboltable.methods.get(checkmethod) != null){
            throw new RuntimeException("MethodDeclaration: Already declared: function "+type+" "+name);
        }
        else{
            Methods addMethod = new Methods(name, symboltable.currentclass.name, type);
            symboltable.currentmethod = addMethod;
            symboltable.methods.put(checkmethod, addMethod);
            symboltable.scope = "Args";
            n.f4.accept(this, argu);
            int overldresult = symboltable.overloading(symboltable.currentclass, addMethod);
            if(overldresult == -2) throw new RuntimeException("MethodDeclaration: Already declared: function "+type+" "+name+" with other args or type method.");
            symboltable.scope = "Vars";
            n.f7.accept(this, argu);
        }
        return _ret;
    }

    public String visit(ClassExtendsDeclaration n, String argu) {
      String _ret=null;
      String name, parentname;
      name = n.f1.accept(this, argu);
      parentname = n.f3.accept(this, argu);
      if (symboltable.classes.get(name) != null){
          throw new RuntimeException("ClassExtendsDeclaration: Already declared: class "+name);
      }
      else{
          if (symboltable.classes.get(parentname) == null){
              throw new RuntimeException("ClassExtendsDeclaration: Parent class doesn't exist: class "+parentname);
          }
          else{
              Classes addClass = new Classes(name, parentname);
              symboltable.classes.put(name, addClass);
              symboltable.currentclass = addClass;
              symboltable.scope = "Class";
              n.f5.accept(this, argu);
              n.f6.accept(this, argu);
          }
      }
      return _ret;
    }
}
