class test1{
	public static void main(String[] r) {
        B b;
        A a;
        a = ((new B().returnB()).returnA()).returnA();

	}
}

class A{
    int i;
    public A returnA(){
        return new A();
    }
}

class B extends A{
    boolean i;
    A a;
    public A returnA(){
        System.out.println(404);
        return new A();
    }

    public B returnB(){
        return new B();
    }
}
