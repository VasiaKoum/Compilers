import visitor.GJDepthFirst;
import syntaxtree.*;

public class EvalVisitor extends GJDepthFirst<String, String>{
    /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
   public String visit(VarDeclaration n, String argu) {
      String _ret=null;
      String type, id;
      type = n.f0.accept(this, argu);
      id = n.f1.accept(this, argu);
      System.out.println(argu+type+" "+id);
      n.f2.accept(this, argu);
      return _ret;
   }

}
