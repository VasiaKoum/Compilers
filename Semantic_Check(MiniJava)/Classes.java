import java.util.*;
import java.io.*;

class Classes{
    String name;
    LinkedHashMap<String, Variables> vars;
    LinkedHashMap<String, Methods> methods;

    public Classes(String nameinput){
        name = nameinput;
        vars = new LinkedHashMap<String, Variables>();
        methods = new LinkedHashMap<String, Methods>();
    }

}
