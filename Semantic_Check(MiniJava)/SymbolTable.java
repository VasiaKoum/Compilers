import visitor.GJDepthFirst;
import syntaxtree.*;
import java.util.*;
import java.io.*;

class SymbolTable{
    LinkedHashMap<String, Classes> classes;
    LinkedHashMap<String, Methods> methods;
    Classes currentclass;
    Methods currentmethod;
    static int varoffset;
    static int methodoffset;
    static String currenttypevar;
    static Boolean classmethod;

    public SymbolTable(){
        this.classes = new LinkedHashMap<String, Classes>();
        this.methods = new LinkedHashMap<String, Methods>();
        // this.varoffset = 0; this.methodoffset = 0;
        this.currenttypevar = "";
    }

    public Variables findvarIn(String nclass, String nmethod, String nvar){
        Variables var=null;
        if (nmethod!=null){
            if(methods.get(nclass+nmethod).vars.get(nvar)!=null) var = methods.get(nclass+nmethod).vars.get(nvar);
            else if(methods.get(nclass+nmethod).args.get(nvar)!=null) var = methods.get(nclass+nmethod).args.get(nvar);
        }
        else if(nclass!=null){
            if(classes.get(nclass).vars.get(nvar)!=null) var = classes.get(nclass).vars.get(nvar);
        }
        return var;
    }

    public Variables findvar(String nclass, String nmethod, String nvar){
        Variables var=null;
        if(methods.get(nclass+nmethod)!=null){
            if(methods.get(nclass+nmethod).vars.get(nvar)!=null) var = methods.get(nclass+nmethod).vars.get(nvar);
            else if(methods.get(nclass+nmethod).args.get(nvar)!=null) var = methods.get(nclass+nmethod).args.get(nvar);
            else if(classes.get(nclass)!=null){
                if(classes.get(nclass).vars.get(nvar)!=null) var = classes.get(nclass).vars.get(nvar);
                else if(classes.get(nclass).parent!=null){
                    String parentname = classes.get(nclass).parent;
                    if(classes.get(parentname).vars.get(nvar)!=null) var = classes.get(parentname).vars.get(nvar);
                }
            }
        }
        return var;
    }
}
