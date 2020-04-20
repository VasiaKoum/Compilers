import java.util.*;
import java.io.*;
// import Variables;

class Methods{
    String name;
    String classpar;
    String typevar;
    LinkedHashMap<String, Variables> args_vars;
    int offset;

    public Methods(String nameinput, String classinput, String typeinput, int beginoffset){
        name = nameinput;
        classpar = classinput;
        typevar = typeinput;
        args_vars = new LinkedHashMap<String, Variables>();
    }
}
