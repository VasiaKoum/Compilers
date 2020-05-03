import java.util.*;
import java.io.*;

class Classes{
    String name;
    String parent;
    LinkedHashMap<String, Variables> vars;
    int varoffset;
    int methodoffset;

    public Classes(String nameinput, String parentinput){
        this.name = nameinput;
        this.parent = parentinput;
        this.vars = new LinkedHashMap<String, Variables>();
        this.varoffset = 0; this.methodoffset = 0;
    }
}
