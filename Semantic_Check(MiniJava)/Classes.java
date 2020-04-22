import java.util.*;
import java.io.*;

class Classes{
    String name;
    LinkedHashMap<String, Variables> vars;
    int varoffset;
    int methodoffset;

    public Classes(String nameinput){
        this.name = nameinput;
        this.vars = new LinkedHashMap<String, Variables>();
        // this.varoffset = 0; SymbolTable.varoffset = 0;
        // this.methodoffset = 0; SymbolTable.methodoffset = 0;
        // SymbolTable.classname = nameinput;
    }

}
