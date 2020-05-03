import visitor.GJDepthFirst;
import syntaxtree.*;
import java.util.*;
import java.io.*;

class SymbolTable{
    LinkedHashMap<String, Classes> classes;
    LinkedHashMap<String, Methods> methods;
    Classes currentclass;
    Methods currentmethod;
    static String scope;
    static String methodpars;
    static int numpars;
    static int numargs;

    public SymbolTable(){
        this.classes = new LinkedHashMap<String, Classes>();
        this.methods = new LinkedHashMap<String, Methods>();
        this.scope = ""; this.methodpars = ""; this.numpars = 0; this.numargs = 0;
    }

    public void putvar(SymbolTable oldst, String nclass, String nmethod, Variables nvar, String scope){
        if(scope.equals("Class")){
            if(classes.get(nclass)!=null){
                if(nvar.type.equals("int") || nvar.type.equals("boolean") || nvar.type.equals("int[]") || nvar.type.equals("boolean[]"))
                    classes.get(nclass).vars.put(nvar.name, nvar);
                else{
                    if(oldst!=null){
                        if(oldst.classes.get(nvar.type)!=null) classes.get(nclass).vars.put(nvar.name, nvar);
                        else {
                            throw new RuntimeException("Cannot find symbol: "+nvar.type+" in var declaration.");
                        }
                    }
                }
            }
        }
        else if(scope.equals("Vars")){
            if(methods.get(nclass+nmethod)!=null){ methods.get(nclass+nmethod).vars.put(nvar.name, nvar);  }
        }
        else if(scope.equals("Args")){
            if(methods.get(nclass+nmethod)!=null){ methods.get(nclass+nmethod).args.put(nvar.name, nvar); }
        }
    }

    public void vardecl(String nclass, String nmethod, Variables nvar, String scope){
        if(scope.equals("Class")){
            if(classes.get(nclass)!=null){
                if(classes.get(nclass).vars.get(nvar.name)==null){
                    classes.get(nclass).vars.put(nvar.name, nvar);
                }
                else{
                    throw new RuntimeException("Variable "+nvar.name+" is already defined in class.");
                }
            }
        }
        else if(scope.equals("Vars")){
            if(methods.get(nclass+nmethod)!=null){
                if((methods.get(nclass+nmethod).args.get(nvar.name)==null) && (methods.get(nclass+nmethod).vars.get(nvar.name)==null)){
                    methods.get(nclass+nmethod).vars.put(nvar.name, nvar);
                }
                else{
                    throw new RuntimeException("Variable "+nvar.name+" is already defined in method.");
                }
            }
        }
        else if(scope.equals("Args")){
            if(methods.get(nclass+nmethod)!=null){
                if(methods.get(nclass+nmethod).args.get(nvar.name)==null){
                    methods.get(nclass+nmethod).args.put(nvar.name, nvar);
                }
                else{
                    throw new RuntimeException("Variable "+nvar.name+" is already defined in method.");
                }
            }
        }
    }

    public Variables findvar(String nclass, String nmethod, String nvar){
        Variables var=null;
        boolean not_found=true;
        if(methods.get(nclass+nmethod)!=null){
            if(methods.get(nclass+nmethod).vars.get(nvar)!=null) var = methods.get(nclass+nmethod).vars.get(nvar);
            else if(methods.get(nclass+nmethod).args.get(nvar)!=null) var = methods.get(nclass+nmethod).args.get(nvar);
            else if(classes.get(nclass)!=null){
                if(classes.get(nclass).vars.get(nvar)!=null) var = classes.get(nclass).vars.get(nvar);
                else{
                    String parentname;
                    if(classes.get(nclass).parent!=null){
                        parentname = classes.get(nclass).parent;
                        while(not_found){
                            if(classes.get(parentname).vars.get(nvar)!=null) {
                                var = classes.get(parentname).vars.get(nvar);
                                not_found = false;
                            }
                            else{
                                if(classes.get(parentname).parent!=null) parentname = classes.get(parentname).parent;
                                else not_found = false;
                            }
                        }
                    }
                }
            }
        }
        return var;
    }

    public Methods findmethod(SymbolTable oldst, String nclass, String nmethod, String primname, String methodname){
        Methods method = null;
        Variables var = findvar(nclass, nmethod, primname);
        boolean not_found = true;
        if(var == null) nclass = primname;
        else nclass = var.type;
        nmethod = methodname;
        if(oldst.classes.get(nclass)!=null){
            if(oldst.methods.get(nclass+nmethod)!=null) method = oldst.methods.get(nclass+nmethod);
            else{
                String parentname;
                if(oldst.classes.get(nclass).parent!=null){
                    parentname = oldst.classes.get(nclass).parent;
                    while(not_found){
                        if(oldst.methods.get(parentname+nmethod)!=null) {
                            method = oldst.methods.get(parentname+nmethod);
                            not_found = false;
                        }
                        else{
                            if(oldst.classes.get(parentname).parent!=null) parentname = oldst.classes.get(parentname).parent;
                            else not_found = false;
                        }
                    }
                }
            }
        }
        return method;
    }

    public boolean checkparent(SymbolTable oldst, String classA, String classB){
        // Check if classB is subclass of classA (for multilevel inheritance), for assignment: eg. A a = new B()
        boolean returned = false;
        Classes parent;
        boolean not_found = false;
        if((parent = oldst.classes.get(classB))!=null){
            if(oldst.classes.get(classA)!=null){
                while(!not_found){
                    if(parent.name.equals(classA)){ not_found = true; returned = true; }
                    else{
                        if(oldst.classes.get(parent.parent)!=null) parent = oldst.classes.get(parent.parent);
                        else not_found = true;
                    }
                }
            }
        }
        return returned;
    }

    public boolean isclasstype(SymbolTable oldst, String classname){
        boolean returned = false;
        if(oldst.classes.get(classname)!=null){ returned = true; }
        return returned;
    }

    // -1 -> not found similar method, -2-> found similar method NOT SAME, 0>-> found similar method AND SAME
    public int overloading(Classes nclass, Methods nmethod){
        int returned = -1;
        Classes parent;
        Methods parentmethod;
        boolean not_found = false;
        if((parent = classes.get(nclass.parent))!=null){
            while(!not_found){
                if((parentmethod = methods.get(parent.name+nmethod.name))!=null){
                    if(parentmethod.name.equals(nmethod.name) && parentmethod.type.equals(nmethod.type)){
                        returned = -2;
                        if(ArgstoString(nmethod.args).equals(ArgstoString(parentmethod.args))){
                            not_found = true; returned = parentmethod.offset;
                        }
                    }
                }
                if(classes.get(parent.parent)!=null) parent = classes.get(parent.parent);
                else not_found = true;
            }
        }
        return returned;
    }

    public String ArgstoString(LinkedHashMap<String, Variables> hm){
        String returned = "";
        for (String key : hm.keySet()) {
            if(returned.equals("")) returned = hm.get(key).type;
            else returned = returned+","+hm.get(key).type;
        }
        return returned;
    }

    public void addoffsets(){
        int i=0;
        for (String keyclass : classes.keySet()) {
            if(i!=0){
                System.out.println("-----------Class "+classes.get(keyclass).name+"-----------");
                System.out.println("--Variables---");
                boolean inparent = false;
                for (String keyvars : classes.get(keyclass).vars.keySet()) {
                    String parentname;
                    if((!inparent) && ((parentname = classes.get(keyclass).parent)!=null)){
                        classes.get(keyclass).varoffset = classes.get(parentname).varoffset;
                        inparent = true;
                    }
                    classes.get(keyclass).vars.get(keyvars).offset = classes.get(keyclass).varoffset;
                    Variables var = classes.get(keyclass).vars.get(keyvars);
                    System.out.println(classes.get(keyclass).name+"."+var.name+" : "+var.offset);

                    if(var.type.equals("int")) classes.get(keyclass).varoffset+=4;
                    else if(var.type.equals("boolean")) classes.get(keyclass).varoffset+=1;
                    else classes.get(keyclass).varoffset+=8;
                }

                System.out.println("---Methods---");
                boolean inmethparent = false;
                for (String keymethod : methods.keySet()) {
                    if(methods.get(keymethod).classpar.equals(classes.get(keyclass).name)){
                        String parentname;
                        boolean overl = false;
                        if((parentname = classes.get(keyclass).parent)!=null){
                            if(!inmethparent) {
                                classes.get(keyclass).methodoffset = classes.get(parentname).methodoffset;
                                inmethparent = true;
                            }
                            int methodoffset;
                            if((methodoffset = overloading(classes.get(keyclass), methods.get(keymethod)))>=0){
                                methods.get(keymethod).offset = methodoffset;
                                overl = true;
                            }
                        }
                        if(!overl){
                            methods.get(keymethod).offset = classes.get(keyclass).methodoffset;
                            Methods method = methods.get(keymethod);
                            System.out.println(classes.get(keyclass).name+"."+method.name+" : "+method.offset);
                            classes.get(keyclass).methodoffset+=8;
                        }
                    }
                }
                System.out.println("\n");
            }
            i++;
        }


    }


    public void IterateSymbolTable(){
        System.out.println("\n\nLINKEDHASHMAP printing: ");
        for (String keyclass : classes.keySet()) {
            System.out.println(classes.get(keyclass).name+" : \t("+classes.get(keyclass).parent+")");
            for (String keyvars : classes.get(keyclass).vars.keySet()) {
                System.out.println("\t"+classes.get(keyclass).vars.get(keyvars).type+" "+classes.get(keyclass).vars.get(keyvars).name);
            }
        }

        for (String keymethod : methods.keySet()) {
            System.out.println(methods.get(keymethod).type+" "+methods.get(keymethod).name);
            for (String keyvars : methods.get(keymethod).args.keySet()) {
                System.out.println("\t["+methods.get(keymethod).args.get(keyvars).type+" "+methods.get(keymethod).args.get(keyvars).name+"]");
            }
            for (String keyvars : methods.get(keymethod).vars.keySet()) {
                System.out.println("\t"+methods.get(keymethod).vars.get(keyvars).type+" "+methods.get(keymethod).vars.get(keyvars).name);
            }
        }
    }

}
