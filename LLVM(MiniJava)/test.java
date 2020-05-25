class Classes {
	public static void main(String[] a) {
		Base b;
		Derived d;
		int r;

  		b = new Base();
 		d = new Derived();

		System.out.println(b.set(1));
		b = d;
		System.out.println(b.get(r, 3));
	}
}

class Base {
	int data;
	public int set(int x) {
		data = x;
		return data;
	}

	public boolean pou(){
		return true;
	}

	public int get(int x, int y) {
		return data;
	}
}

class Derived extends Base {
	public int set(int x) {
		data = x * 2;
		return data;
	}

	public int edw(){
		return 3;
	}

	public int comp(){
		return 6;
	}
}


class Vasia extends Derived{

}
