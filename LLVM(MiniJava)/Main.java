import syntaxtree.*;
import visitor.*;
import java.util.*;
import java.io.*;

class Main{
	public static void main(String [] args){
		if(args.length==0){
			System.err.println("<inputFile> missing");
			System.exit(-1);
		}
		for(int i=0; i<args.length; i++) {
            FileInputStream flsin = null;
			try{
				System.err.println("FILE ["+args[i]+"]");
            	flsin = new FileInputStream(args[i]);
            	MiniJavaParser parser = new MiniJavaParser(flsin);
            	Goal root = parser.Goal();

				SymbolTable symboltable = new SymbolTable();
				SymbolTable finalsymboltable = new SymbolTable();

				VisitorSymbolTable visitorsymboltable = new VisitorSymbolTable(symboltable);
				root.accept(visitorsymboltable, null);

				TypeChecking typechecking = new TypeChecking(symboltable, finalsymboltable);
				root.accept(typechecking, null);

				finalsymboltable.addoffsets();
				// System.err.println("Program compiled successfully!");
				
				GenCode llvmgen = new GenCode(finalsymboltable);
				// root.accept(llvmgen, null);
			}
			catch(RuntimeException ex){
				System.out.println("Compilation error at:");
				System.out.println(ex.getMessage()+"\n");
				continue;
			}
			catch(ParseException ex){
				System.out.println(ex.getMessage());
			}
			catch(FileNotFoundException ex){
				System.err.println(ex.getMessage());
			}
			finally{
				try{
					if(flsin!=null) flsin.close();
				}
				catch(IOException ex){
					System.err.println(ex.getMessage());
				}
			}
		}
	}

}
