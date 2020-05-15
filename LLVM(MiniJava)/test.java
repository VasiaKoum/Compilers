class Classes {
	public static void main(String[] a) {
		Base b;
		Derived d;
  		b = new Base();
 		d = new Derived();
		System.out.println(b.set(1));
		b = d;
		System.out.println(b.set(3));
	}
}

class Base {
	int data;
	public int set(int x) {
		data = x;
		return data;
	}

	public int get() {
		return data;
	}
}

class Derived extends Base {
	public int set(int x) {
		Base b;
		data = x * 2;
		return x;
	}

    public int ret(int x) {
        data = x * 2;
        return data;
    }
}

class Derived2 extends Derived {
	int a;
	int b;
	int c;
	int d;
	public int vasia(int x) {
		data = x * 2;
		a = b * b;
		c = d * 2;
		return data;
	}

    public Base vasvas(int x, int r) {
		Base b;
        data = x * 2;
        return b;
    }
}
