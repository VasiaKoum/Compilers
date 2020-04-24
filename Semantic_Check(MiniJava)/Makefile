all: compile

c_all:
	java -jar jtb132di.jar minijava.jj
	java -jar javacc5.jar minijava-jtb.jj
	javac VisitorSymbolTable.java
	javac Main.java

compile:
	javac VisitorSymbolTable.java
	javac Main.java

r_all:
	./run_test_files.sh ./test_files/minijava-error

clean:
	rm -f *.class *~
