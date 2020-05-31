all: compile

c_all:
	java -jar jtb132di.jar minijava.jj
	java -jar javacc5.jar minijava-jtb.jj
	javac Main.java

compile:
	@javac Main.java

clean:
	rm -f *.class *~
