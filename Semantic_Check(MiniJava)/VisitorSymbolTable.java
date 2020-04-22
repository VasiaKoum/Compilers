import visitor.GJDepthFirst;
import syntaxtree.*;
import java.util.*;
import java.io.*;

public class VisitorSymbolTable extends GJDepthFirst<String, String>{
    SymbolTable symboltable;

    public VisitorSymbolTable(SymbolTable st){
        symboltable = st;
    }

    public String visit(NodeToken n, String argu) { return n.toString(); }

    /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
   public String visit(VarDeclaration n, String argu) {
      String _ret=null;
      String type = n.f0.accept(this, argu);
      String id = n.f1.accept(this, argu);
      LinkedHashMap<String, Variables> inscope;
      if (symboltable.classmethod) inscope = symboltable.classes.get(symboltable.currentclass).vars;
      else inscope = symboltable.methods.get(symboltable.currentmethod).vars;

      if (inscope.get(id) != null){
          System.out.println("Already declared!");
          // throw new RuntimeException();
      }
      else{
          // System.out.println(type+" "+id+" -> "+putvars.offset);
          if (symboltable.classmethod) System.out.println("in scope: "+symboltable.currentclass+" "+id+" "+type);
          else System.out.println("in scope: "+symboltable.currentmethod+" "+id+" "+type);
          inscope.put(id, new Variables(id, type));
      }
      return _ret;
   }

   /**
    * f0 -> "boolean"
    * f1 -> "["
    * f2 -> "]"
    */
   public String visit(BooleanArrayType n, String argu) {
      return "boolean[]";
   }

   /**
    * f0 -> "int"
    * f1 -> "["
    * f2 -> "]"
    */
   public String visit(IntegerArrayType n, String argu) {
      return "int[]";
   }

   /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> ( VarDeclaration() )*
    * f4 -> ( MethodDeclaration() )*
    * f5 -> "}"
    */
   public String visit(ClassDeclaration n, String argu) {
      String _ret=null;
      String name = n.f1.accept(this, argu);
      if (symboltable.classes.get(name) != null){
          System.out.println("Already declared!");
          // throw new RuntimeException();
      }
      else{
          System.out.println(name+":");
          symboltable.classes.put(name, new Classes(name));
          symboltable.currentclass = name;
          symboltable.classmethod = true;
          n.f3.accept(this, argu);
          n.f4.accept(this, argu);
      }
      return _ret;
   }

   /**
    * f0 -> "public"
    * f1 -> Type()
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( FormalParameterList() )?
    * f5 -> ")"
    * f6 -> "{"
    * f7 -> ( VarDeclaration() )*
    * f8 -> ( Statement() )*
    * f9 -> "return"
    * f10 -> Expression()
    * f11 -> ";"
    * f12 -> "}"
    */
   public String visit(MethodDeclaration n, String argu) {
      String _ret=null;
      String name = n.f2.accept(this, argu);
      String type = n.f1.accept(this, argu);
      if (symboltable.methods.get(name) != null){
          System.out.println("Already declared!");
          // throw new RuntimeException();
      }
      else{
          System.out.println(name+":");
          symboltable.currentmethod = name;
          symboltable.classmethod = false;
          symboltable.methods.put(name, new Methods(name, symboltable.currentclass, type));
          n.f7.accept(this, argu);
      }
      return _ret;
   }

   /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "extends"
    * f3 -> Identifier()
    * f4 -> "{"
    * f5 -> ( VarDeclaration() )*
    * f6 -> ( MethodDeclaration() )*
    * f7 -> "}"
    */
   // public String visit(ClassExtendsDeclaration n, String argu) {
   //    String _ret=null;
   //    String name, parentname;
   //    name = n.f1.accept(this, argu);
   //    // parentname = n.f3.accept(this, argu);
   //    // n.f5.accept(this, name+" :: "+parentname+" :: ");
   //    // n.f6.accept(this, name+" :: "+parentname+" :: ");
   //    return _ret;
   // }
}
