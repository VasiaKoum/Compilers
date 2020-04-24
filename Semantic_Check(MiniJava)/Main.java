import syntaxtree.*;
import visitor.*;
import java.util.*;
import java.io.*;

class Main{
	public static void main(String [] args){
		if(args.length==0){
			System.err.println("Usage: java Driver <inputFile>");
			System.exit(-1);
		}

		for(int i=0; i<args.length; i++) {
            FileInputStream flsin = null;
			try{
            	flsin = new FileInputStream(args[i]);
            	MiniJavaParser parser = new MiniJavaParser(flsin);
            	Goal root = parser.Goal();
            	System.err.println("Program parsed successfully!");

				SymbolTable symboltable = new SymbolTable();
				SymbolTable finalsymboltable = new SymbolTable();

				VisitorSymbolTable visitorsymboltable = new VisitorSymbolTable(symboltable);
				root.accept(visitorsymboltable, null);

				System.out.println("TYPE-CHECKING:");
				TypeChecking typechecking = new TypeChecking(symboltable, finalsymboltable);
				root.accept(typechecking, null);
				System.err.println("Program compiled successfully!");
				// typechecking.IterateHashMap();
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
