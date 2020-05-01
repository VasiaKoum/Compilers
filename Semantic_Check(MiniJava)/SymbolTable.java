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

    // FIX FOR VisitorSymbolTable
    static Boolean classmethod;

    public SymbolTable(){
        this.classes = new LinkedHashMap<String, Classes>();
        this.methods = new LinkedHashMap<String, Methods>();
        this.scope = "";
    }

    public void putvar(SymbolTable oldst, String nclass, String nmethod, Variables nvar, String scope){
        if(scope == "Class"){
            if(classes.get(nclass)!=null){
                if(nvar.type == "int" || nvar.type == "boolean" || nvar.type == "int[]" || nvar.type == "boolean[]")
                    classes.get(nclass).vars.put(nvar.name, nvar);
                else{
                    if(oldst!=null){
                        if(oldst.classes.get(nvar.type)!=null) classes.get(nclass).vars.put(nvar.name, nvar);
                        else {
                            System.out.println("Cannot find symbol: "+nvar.type+" in var declaration.");
                            System.exit(0);
                        }
                    }
                }
            }
        }
        else if(scope == "Vars"){
            if(methods.get(nclass+nmethod)!=null){ methods.get(nclass+nmethod).vars.put(nvar.name, nvar);  }
        }
        else if(scope == "Args"){
            if(methods.get(nclass+nmethod)!=null){ methods.get(nclass+nmethod).args.put(nvar.name, nvar); }
        }
    }

    public void vardecl(String nclass, String nmethod, Variables nvar, String scope){
        if(scope == "Class"){
            if(classes.get(nclass)!=null){
                if(classes.get(nclass).vars.get(nvar.name)==null){
                    classes.get(nclass).vars.put(nvar.name, nvar);
                }
                else{
                    System.out.println("Variable "+nvar.name+" is already defined in class.");
                    System.exit(0);
                }
            }
        }
        else if(scope == "Vars"){
            if(methods.get(nclass+nmethod)!=null){
                if((methods.get(nclass+nmethod).args.get(nvar.name)==null) && (methods.get(nclass+nmethod).vars.get(nvar.name)==null)){
                    methods.get(nclass+nmethod).vars.put(nvar.name, nvar);
                }
                else{
                    System.out.println("Variable "+nvar.name+" is already defined in method.");
                    System.exit(0);
                }
            }
        }
        else if(scope == "Args"){
            if(methods.get(nclass+nmethod)!=null){
                if(methods.get(nclass+nmethod).args.get(nvar.name)==null){
                    methods.get(nclass+nmethod).args.put(nvar.name, nvar);
                }
                else{
                    System.out.println("Variable "+nvar.name+" is already defined in method.");
                    System.exit(0);
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

    public Methods findmethod(String nclass, String nmethod, String primname, String methodname){
        Methods method = null;
        Variables var = null;
        var = findvar(nclass, nmethod, primname);
        if(var!=null){
            method = methods.get(var.type+methodname);
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
                    if(parent.name == classA){ not_found = true; returned = true; }
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
