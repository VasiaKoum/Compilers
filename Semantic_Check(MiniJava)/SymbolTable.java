import visitor.GJDepthFirst;
import syntaxtree.*;
import java.util.*;
import java.io.*;

public class SymbolTable extends GJDepthFirst<String, LinkedHashMap>{
    LinkedHashMap<String, Classes> mapclass;
    static int varoffset;
    static int methodoffset;
    static String classname;
    static String methodname;

    public SymbolTable(){
        mapclass = new LinkedHashMap<String, Classes>();
        // this.varoffset = 0; this.methodoffset = 0;
        // this.classname = ""; this.methodname = "";
    }

    public String visit(NodeToken n, LinkedHashMap argu) { return n.toString(); }

    /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
   public String visit(VarDeclaration n, LinkedHashMap argu) {
      String _ret=null;
      String type = n.f0.accept(this, argu);
      String id = n.f1.accept(this, argu);
      if (argu.get(id) != null){
          System.out.println("Already declared!");
          // throw new RuntimeException();
      }
      else{
          Variables putvars = new Variables(id, type);
          System.out.println(type+" "+id+" -> "+putvars.offset);
          argu.put(id, putvars);
      }
      return _ret;
   }

   /**
    * f0 -> "boolean"
    * f1 -> "["
    * f2 -> "]"
    */
   public String visit(BooleanArrayType n, LinkedHashMap argu) {
      return "boolean[]";
   }

   /**
    * f0 -> "int"
    * f1 -> "["
    * f2 -> "]"
    */
   public String visit(IntegerArrayType n, LinkedHashMap argu) {
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
   public String visit(ClassDeclaration n, LinkedHashMap argu) {
      String _ret=null;
      String name = n.f1.accept(this, argu);
      if (mapclass.get(name) != null){
          System.out.println("Already declared!");
          // throw new RuntimeException();
      }
      else{
          System.out.println(name+":");
          Classes putclass = new Classes(name);
          mapclass.put(name, putclass);
          n.f3.accept(this, putclass.vars);
          n.f4.accept(this, putclass.methods);
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
   public String visit(MethodDeclaration n, LinkedHashMap argu) {
      String _ret=null;
      String name = n.f2.accept(this, argu);
      String type = n.f1.accept(this, argu);
      if (argu.get(name) != null){
          System.out.println("Already declared!");
          // throw new RuntimeException();
      }
      else{
          System.out.println(name+":");
          Methods putclass = new Methods(name, classname, type);
          argu.put(name, putclass);
          n.f7.accept(this, putclass.vars);
          // n.f4.accept(this, putclass.methods);
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
   public String visit(ClassExtendsDeclaration n, LinkedHashMap argu) {
      String _ret=null;
      String name, parentname;
      name = n.f1.accept(this, argu);
      // parentname = n.f3.accept(this, argu);
      // n.f5.accept(this, name+" :: "+parentname+" :: ");
      // n.f6.accept(this, name+" :: "+parentname+" :: ");
      return _ret;
   }

   // public int getlastvaroffset(String classname, LinkedHashMap argu){
   //     Classes thisclass = mapclass.get(classname);
   //     if ( thisclass != null){ return thisclass.varoffset; }
   //     else { return 0; }
   // }
   //
   // public int getlastmethodoffset(String methodname, LinkedHashMap argu){
   //     Classes thismethod = mapclass.get(methodname);
   //     if ( thismethod != null){ return thismethod.methodoffset; }
   //     else { return 0; }
   // }
}
