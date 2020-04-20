import java.util.*;
import java.io.*;
// import Variables;

class Methods{
    String name;
    String classpar;
    String typevar;
    LinkedHashMap<String, Variables> args_vars;
    LinkedHashMap<String, Variables> vars;
    int offset;

    public Methods(String nameinput, String classinput, String typeinput){
        this.name = nameinput;
        this.classpar = classinput;
        this.typevar = typeinput;
        this.args_vars = new LinkedHashMap<String, Variables>();
        this.vars = new LinkedHashMap<String, Variables>();
        SymbolTable.methodoffset+=8; offset = SymbolTable.methodoffset;
        SymbolTable.methodname = nameinput;
    }
}
