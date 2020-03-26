JC = javac
JVM= java

.SUFFIXES: .java .class

.java.class:
		$(JC) $*.java

CLASSES = \
		CalculatorParser.java \
		CalculatorEval.java

default: classes

classes: $(CLASSES:.java=.class)

runp: CalculatorParser.class
	@$(JVM) CalculatorParser
rune: CalculatorEval.class
	@$(JVM) CalculatorEval

clean:
	$(RM) *.class
