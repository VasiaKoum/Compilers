import visitor.GJDepthFirst;
import syntaxtree.*;
import java.util.*;
import java.io.*;

class SymbolTable{
    LinkedHashMap<String, Classes> classes;
    LinkedHashMap<String, Methods> methods;
    static int varoffset;
    static int methodoffset;
    static String currentclass;
    static String currentmethod;
    static Boolean classmethod;

    public SymbolTable(){
        this.classes = new LinkedHashMap<String, Classes>();
        this.methods = new LinkedHashMap<String, Methods>();
        // this.varoffset = 0; this.methodoffset = 0;
        this.currentclass = ""; this.currentmethod = "";
    }
}
