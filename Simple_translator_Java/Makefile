all: compile

compile:
	jflex scanner.flex
	java -jar java-cup-11b.jar -interface -parser Parser parser.cup
	javac -cp java-cup-11b-runtime.jar *.java

run1:
	@java -cp java-cup-11b-runtime.jar:. Main < ./inputs/test.txt

run2:
	@java -cp java-cup-11b-runtime.jar:. Main < ./inputs/input2.txt

run3:
	@java -cp java-cup-11b-runtime.jar:. Main < ./inputs/input3.txt

clean:
	rm -f *.class *~
