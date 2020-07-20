all: compile

c_all:
	java -jar jtb132di.jar minijava.jj
	java -jar javacc5.jar minijava-jtb.jj
	javac VisitorSymbolTable.java
	javac Main.java

compile:
	javac VisitorSymbolTable.java
	javac Main.java

run_correct:
	./run_test_files.sh ./test_files/minijava

run_error:
	./run_test_files.sh ./test_files/minijava-error
compare_off:
	./compare_offsets.sh ./outputs ./correct_offsets
clean:
	rm -f *.class *~
