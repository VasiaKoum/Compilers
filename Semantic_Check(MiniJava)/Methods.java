import java.util.*;
import java.io.*;
// import Variables;

class Methods{
    String name;
    String type;
    String classpar;
    LinkedHashMap<String, Variables> vars;
    LinkedHashMap<String, Variables> args;
    int offset;

    public Methods(String nameinput, String classinput, String typeinput){
        this.name = nameinput;
        this.classpar = classinput;
        this.type = typeinput;
        this.vars = new LinkedHashMap<String, Variables>();
        this.args = new LinkedHashMap<String, Variables>();
        // SymbolTable.methodoffset+=8; offset = SymbolTable.methodoffset;
        // SymbolTable.methodname = nameinput;
    }
}
