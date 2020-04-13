all: compile

compile_all:
	java -jar jtb132di.jar minijava.jj
	java -jar javacc5.jar minijava-jtb.jj
	javac EvalVisitor.java
	javac Main.java

compile:
	javac EvalVisitor.java
	javac Main.java

clean:
	rm -f *.class *~
