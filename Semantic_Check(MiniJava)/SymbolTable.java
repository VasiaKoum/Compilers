import visitor.GJDepthFirst;
import syntaxtree.*;
import java.util.*;
import java.io.*;

public class SymbolTable extends GJDepthFirst<String, LinkedHashMap>{
    LinkedHashMap<String, Classes> mapclass;
    static int varoffset;
    static int methoffset;

    public SymbolTable(){
        mapclass = new LinkedHashMap<String, Classes>();
        varoffset = 0; methoffset = 0;
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
          Variables putvars = new Variables(id, type, "");
          System.out.println("Declare:["+id+"]"+" ["+type+"]"+" ["+putvars.offset+"]");
          argu.put(id, putvars);
      }
      return _ret;
      // System.out.println(argu+type+" "+id);
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
          // System.out.println(name.getClass().getName());
          Classes putclass = new Classes(name);
          mapclass.put(name, putclass);
          n.f3.accept(this, putclass.vars);
          n.f4.accept(this, putclass.methods);
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


}
