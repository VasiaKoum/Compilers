all: compile

compile:
	jflex scanner.flex
	java -jar java-cup-11b.jar -interface -parser Parser parser.cup
	javac -cp java-cup-11b-runtime.jar *.java

# change the test.txt with other input file
run:
	@java -cp java-cup-11b-runtime.jar:. Main < ./inputs/test.txt

runj:
	@java -cp java-cup-11b-runtime.jar:. Main < ./inputs/test.txt > SimpleParser.java
	@javac SimpleParser.java
	@java SimpleParser

clean:
	rm -f *.class *~

cleanj:
	rm -f *.class *~ SimpleParser.java
