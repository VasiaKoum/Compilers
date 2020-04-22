import java.util.*;
import java.io.*;

class Variables{
    String name;
    String type;
    int offset;

    public Variables(String nameinput, String typeinput){
        this.name = nameinput; this.type = typeinput; this.offset = 0;
        // if (typeinput == "int") { SymbolTable.varoffset+=4; offset = SymbolTable.varoffset; }
        // else if (typeinput == "int[]" || typeinput == "boolean[]") { SymbolTable.varoffset+=8; offset = SymbolTable.varoffset; }
        // else if (typeinput == "boolean") { SymbolTable.varoffset++; offset = SymbolTable.varoffset; }
        // else { SymbolTable.varoffset+=8; offset = SymbolTable.varoffset; }
    }

}
