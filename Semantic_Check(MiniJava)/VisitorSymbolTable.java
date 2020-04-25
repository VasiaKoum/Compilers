import visitor.GJDepthFirst;
import syntaxtree.*;
import java.util.*;
import java.io.*;

public class VisitorSymbolTable extends GJDepthFirst<String, LinkedHashMap<String, Variables> >{
    SymbolTable symboltable;

    public VisitorSymbolTable(SymbolTable st){
        symboltable = st;
    }

    public String visit(MainClass n, LinkedHashMap<String, Variables> argu) {
       String _ret=null;
       String nameclass = n.f1.accept(this, argu);
       String name = "main";
       if (symboltable.classes.get(name) != null){
           System.out.println("Already declared -> class"+name);
           System.exit(0);
       }
       else{
           // System.out.println(name+":");
           Classes addClass = new Classes(name, null);
           symboltable.classes.put(name, addClass);
           symboltable.currentclass = addClass;
           symboltable.classmethod = true;
           n.f14.accept(this, symboltable.classes.get(name).vars);
       }
       return _ret;
    }

    public String visit(NodeToken n, LinkedHashMap<String, Variables> argu) { return n.toString(); }

    public String visit(VarDeclaration n, LinkedHashMap<String, Variables> argu) {
      String _ret=null;
      String type = n.f0.accept(this, argu);
      String name = n.f1.accept(this, argu);
      if (argu.get(name) != null){
          System.out.println("Already declared -> "+type+" "+name);
          System.exit(0);
      }
      else{
          // System.out.println("\t"+type+" "+name);
          argu.put(name, new Variables(name, type));
      }
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
      if (symboltable.classes.get(name) != null){
          System.out.println("Already declared -> class "+name);
          System.exit(0);
      }
      else{
          // System.out.println(name+":");
          Classes addClass = new Classes(name, null);
          symboltable.classes.put(name, addClass);
          symboltable.currentclass = addClass;
          symboltable.classmethod = true;
          n.f3.accept(this, symboltable.classes.get(name).vars);
          n.f4.accept(this, argu);
      }
      return _ret;
    }

    public String visit(FormalParameter n, LinkedHashMap<String, Variables> argu) {
       String _ret=null;
       String name = n.f1.accept(this, argu);
       String type = n.f0.accept(this, argu);
       if (argu.get(name) != null){
           System.out.println("Already declared -> "+type+" "+name);
           System.exit(0);
       }
       else{
           // System.out.println("\t["+type+" "+name+"]");
           argu.put(name, new Variables(name, type));
       }
       return _ret;
    }

    public String visit(MethodDeclaration n, LinkedHashMap<String, Variables> argu) {
      String _ret=null;
      String name = n.f2.accept(this, argu);
      String type = n.f1.accept(this, argu);
      String checkmethod = symboltable.currentclass.name+name;
      if (symboltable.methods.get(checkmethod) != null){
          System.out.println("Already declared -> function "+type+" "+name);
          System.exit(0);
      }
      else{
          // System.out.println(name+":");
          Methods addMethod = new Methods(name, symboltable.currentclass.name, type);
          symboltable.currentmethod = addMethod;
          symboltable.methods.put(checkmethod, addMethod);
          symboltable.classmethod = false;
          n.f4.accept(this, symboltable.methods.get(checkmethod).args);
          n.f7.accept(this, symboltable.methods.get(checkmethod).vars);
      }
      return _ret;
    }

    public String visit(ClassExtendsDeclaration n, LinkedHashMap<String, Variables> argu) {
      String _ret=null;
      String name, parentname;
      name = n.f1.accept(this, argu);
      parentname = n.f3.accept(this, argu);
      if (symboltable.classes.get(name) != null){
          System.out.println("Already declared -> class "+name);
          System.exit(0);
      }
      else{
          if (symboltable.classes.get(parentname) == null){
              System.out.println("Parent class doesn't exist -> class "+parentname);
              System.exit(0);
          }
          else{
              // System.out.println(parentname+":"+name+":");
              Classes addClass = new Classes(name, parentname);
              symboltable.classes.put(name, addClass);
              symboltable.currentclass = addClass;
              symboltable.classmethod = true;
              n.f5.accept(this, symboltable.classes.get(name).vars);
              n.f6.accept(this, argu);
          }
      }
      return _ret;
    }
}
