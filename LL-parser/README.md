# Compilers Project
## LL-parser:

For the first part of this homework you should implement a simple calculator. The calculator should accept expressions with the addition, subtraction, multiplication and division operators, as well as parentheses. The grammar (for multi-digit numbers) is summarized in:

exp -> num | exp op exp | (exp)

op -> + | - | * | /

num -> digit | digit num

digit -> 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9

You need to change this grammar to support priority between the operators, to remove the left recursion for LL parsing, etc.

This part of the homework is divided in two tasks:

1. For practice, you can write the FIRST+ & FOLLOW sets for the LL(1) version of the above grammar. In the end you will summarize them in a single lookahead table (include a row for every derivation in your final grammar). This part will not be graded.

2. You have to write a recursive descent parser in Java that reads expressions and computes the values or prints "parse error" if there is a syntax error. You don't need to identify blank space. You can read the symbols one-by-one (as in the C getchar() function). The expression must end with a newline or EOF.

Your parser should read its input from the standard input (e.g. via an InputStream on System.in) and write the computed values of expressions to the standard output (System.out). Parse errors should be reported on standard error (System.err).

------------
##### Grammar for LL-parser:

```exp1 -> term1 exp2
exp2 -> + term1 exp2
     | - term1 exp2
     | e
term1 -> factor term2
term2 -> * factor term2
       | / factor term2
       | e
factor -> (exp1)
      | num1
num1 -> digit num2
num2 -> digit num2
      | e
digit -> 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
```
##### Lookahead Table:

|  | 0...9 | + | - | / | * | EOF | ( | ) |
|:------:|:-----:|:--:|:--:|:--:|:--:|:---:|:--:|:-:|
| exp1 | #1 |  |  |  |  |  | #1 |  |
| exp2 |  | #2 | #3 |  |  | e |  | e |
| term1 | #5 |  |  |  |  |  | #5 |  |
| term2 |  | e | e | #7 | #6 | e |  | e |
| factor | #10 |  |  |  |  |  | #9 |  |
| num1 | #11 |  |  |  |  |  |  |  |
| num2 | #12 | e | e | e | e | e |  | e |
| digit | #14 |  |  |  |  |  |  |  |

*Empty cells=> error
