import java.util.*;
import java.io.*;

class Variables{
    String name;
    String type;
    String value;
    int offset;

    public Variables(String nameinput, String typeinput, String valueinput){
        name = nameinput; type = typeinput; value = valueinput;
        if (typeinput == "int") { SymbolTable.varoffset+=4; offset = SymbolTable.varoffset; }
        else if (typeinput == "int[]" || typeinput == "boolean[]") { SymbolTable.varoffset+=8; offset = SymbolTable.varoffset; }
        else if (typeinput == "boolean") { SymbolTable.varoffset++; offset = SymbolTable.varoffset; }
        else { SymbolTable.varoffset+=8; offset = SymbolTable.varoffset; }
    }

}
