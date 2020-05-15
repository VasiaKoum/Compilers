import java.util.*;
import java.io.*;

class Methods{
    String name;
    String type;
    String classpar;
    LinkedHashMap<String, Variables> vars;
    LinkedHashMap<String, Variables> args;
    int vplace;
    int offset;

    public Methods(String nameinput, String classinput, String typeinput){
        this.name = nameinput;
        this.classpar = classinput;
        this.type = typeinput;
        this.vars = new LinkedHashMap<String, Variables>();
        this.args = new LinkedHashMap<String, Variables>();
        this.vplace = -1;
        this.offset = 0;
    }

    public int getOffset(){
        return this.offset;
    }
}
