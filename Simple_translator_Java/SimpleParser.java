public class SimpleParser {
public static String reversefunc(String input) {
  	String output="";
	char[] revarray = input.toCharArray();

	for (int i = revarray.length-1; i>=0; i--)
		output = output + revarray[i];
  	return output;
}

public static void main(String[] args){
	System.out.println(newf());
	System.out.println(name(surname("x"), surname("y")));
	System.out.println(surname("x"));
	System.out.println(fullname(name(name(surname("w"), "i"), "p"), " ", surname("v")));
}

public static String name(String x, String y){
	 return (cond_repeat("y", "y").startsWith(x) ? x.startsWith("y") ? x : x.startsWith("4") ? fullname(name(name(surname(reversefunc("compilers")), "u"), "id"), " ", surname(reversefunc(reversefunc("smara")))) : reversefunc(reversefunc(reversefunc(x))) : "5");
}

public static String surname(String x){
	 return (((x)+(name((("e")+(name("r", "tree")))+("vasia"), x)))+("vasia"));
}

public static String fullname(String first_name, String sep, String last_name){
	 return (((first_name)+(sep))+(last_name));
}

public static String cond_repeat(String c, String x){
	 return ("yes".startsWith(c) ? c.startsWith("yes") ? surname(x) : x : x);
}

public static String newf(){
	 return ("Fox".startsWith("F") ? "cool" : ("error")+("Bear".startsWith("B") ? "nice" : "not nice"));
}
}
