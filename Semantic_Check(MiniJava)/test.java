class Test{
    public static void main(String[] a){
    }
}

class A{
    int i;
    boolean flag;
    int j;
    public int foo() {
        return 3;
    }
    public boolean fa() {
        return true;
    }
    public boolean fa2() {
        return true;
    }
    public boolean fa3() {
        return true;
    }
}

class B extends A{
    A type;
    int k;
    public int foo() { //OVERRIDING
        return 4;
    }
    public boolean bla() {
        return false;
    }
    public boolean foo2() {
        return true;
    }
    public boolean fa3() { //OVERRIDING
        return true;
    }
    public boolean foo3() {
        return true;
    }
}
