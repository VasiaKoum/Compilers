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
           // throw new RuntimeException();
       }
       else{
           System.out.println(name+":");
           symboltable.classes.put(name, new Classes(name, null));
           symboltable.currentclass = name;
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
          // throw new RuntimeException();
      }
      else{
          System.out.println("\t"+type+" "+name);
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
          System.out.println("Already declared -> class"+name);
          // throw new RuntimeException();
      }
      else{
          System.out.println(name+":");
          symboltable.classes.put(name, new Classes(name, null));
          symboltable.currentclass = name;
          symboltable.classmethod = true;
          n.f3.accept(this, symboltable.classes.get(name).vars);
          n.f4.accept(this, argu);
      }
      System.out.println("--------------------------------");
      return _ret;
    }

    public String visit(FormalParameter n, LinkedHashMap<String, Variables> argu) {
       String _ret=null;
       String name = n.f1.accept(this, argu);
       String type = n.f0.accept(this, argu);
       if (argu.get(name) != null){
           System.out.println("Already declared -> "+type+" "+name);
           // throw new RuntimeException();
       }
       else{
           System.out.println("\t["+type+" "+name+"]");
           argu.put(name, new Variables(name, type));
       }
       return _ret;
    }

    public String visit(MethodDeclaration n, LinkedHashMap<String, Variables> argu) {
      String _ret=null;
      String name = n.f2.accept(this, argu);
      String type = n.f1.accept(this, argu);
      if (symboltable.methods.get(name) != null){
          System.out.println("Already declared -> function "+type+" "+name);
          // throw new RuntimeException();
      }
      else{
          System.out.println(name+":");
          symboltable.currentmethod = name;
          symboltable.classmethod = false;
          symboltable.methods.put(name, new Methods(name, symboltable.currentclass, type));
          n.f4.accept(this, symboltable.methods.get(name).vars);
          n.f7.accept(this, symboltable.methods.get(name).args);
      }
      return _ret;
    }

    public String visit(ClassExtendsDeclaration n, LinkedHashMap<String, Variables> argu) {
      String _ret=null;
      String name, parentname;
      name = n.f1.accept(this, argu);
      parentname = n.f3.accept(this, argu);
      if (symboltable.classes.get(name) != null){
          System.out.println("Already declared -> class"+name);
          // throw new RuntimeException();
      }
      else{
          if (symboltable.classes.get(parentname) == null){
              System.out.println("Parent class doesn't exist -> class"+parentname);
              // throw new RuntimeException();
          }
          else{
              System.out.println(parentname+":"+name+":");
              symboltable.classes.put(name, new Classes(name, parentname));
              symboltable.currentclass = name;
              symboltable.classmethod = true;
              n.f5.accept(this, symboltable.classes.get(name).vars);
              n.f6.accept(this, argu);
          }
      }
      System.out.println("--------------------------------");
      return _ret;
    }
}
