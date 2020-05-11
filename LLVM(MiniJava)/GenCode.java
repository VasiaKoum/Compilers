import visitor.GJDepthFirst;
import syntaxtree.*;
import java.util.*;
import java.io.*;

public class GenCode extends GJDepthFirst<String, String>{
    SymbolTable symboltable;
    int regnum;

    public GenCode(SymbolTable st){
        this.symboltable = st;
        this.regnum = 0;
    }



}
