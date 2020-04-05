import java_cup.runtime.*;
import java.io.*;
import java.nio.file.Paths;
import java.nio.file.Files;

class Main {
    public static void main(String[] argv) throws Exception{
        System.out.println("public class SimpleParser {");
        String content; content = new String(Files.readAllBytes(Paths.get("Reverse")));
        System.out.println(content);
        Parser p = new Parser(new Scanner(new InputStreamReader(System.in)));
        p.parse();
        System.out.println("}");
    }
}
