import java.util.*;
import java.io.*;

class Classes{
    String name;
    LinkedHashMap<String, Variables> vars;
    LinkedHashMap<String, Methods> methods;
    int varoffset;
    int methodoffset;

    public Classes(String nameinput){
        this.name = nameinput;
        this.vars = new LinkedHashMap<String, Variables>();
        this.methods = new LinkedHashMap<String, Methods>();
        this.varoffset = 0; SymbolTable.varoffset = 0;
        this.methodoffset = 0; SymbolTable.methodoffset = 0;
        SymbolTable.classname = nameinput;
    }

}
