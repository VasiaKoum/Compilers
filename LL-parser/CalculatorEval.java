import java.io.InputStream;
import java.io.IOException;

class CalculatorEval{
    private int lookaheadToken, x;
    private InputStream in;

    public CalculatorEval(InputStream in) throws IOException{
        this.in = in;
        lookaheadToken = in.read();
    }
    private void consume(int symbol) throws IOException, ParseError{
        if (lookaheadToken != symbol)
            throw new ParseError();
        lookaheadToken = in.read();
    }
    private int evalDigit(int digit){ return digit - '0'; }

    private int Exp1() throws IOException, ParseError{
        // System.out.printf("Exp1: lookaheadToken: [%c]\n", lookaheadToken);
        int result=-1;
        if((lookaheadToken >= '0' && lookaheadToken <= '9') || lookaheadToken == '('){
            int number = Term1();
            result = Exp2(number);
        }
        else throw new ParseError();
        return result;
    }

    private int Exp2(int input) throws IOException, ParseError{
        // System.out.printf("Exp2: lookaheadToken: [%c]\n", lookaheadToken);
        int returnResult=-1, result=0;
        if(lookaheadToken == '+' || lookaheadToken == '-'){
            consume(lookaheadToken);
            int number = Term1();
            if (lookaheadToken == '+') result = number+input;
            if (lookaheadToken == '-') result = number-input;
            returnResult = Exp2(result);
        }
        else throw new ParseError();
        return returnResult;
    }

    private int Term1() throws IOException, ParseError{
        // System.out.printf("Term1: lookaheadToken: [%c]\n", lookaheadToken);
        int result = -1;
        if((lookaheadToken >= '0' && lookaheadToken <= '9') || lookaheadToken == '('){
            int number = Factor();
            result = Term2(number);
        }
        else throw new ParseError();
        return result;
    }

    private int Term2(int input) throws IOException, ParseError{
        // System.out.printf("Term2: lookaheadToken: [%c]\n", lookaheadToken);
        x = lookaheadToken;
        int returnResult=-1, result=0;
        if((lookaheadToken >= '0' && lookaheadToken <= '9') || lookaheadToken == '(') throw new ParseError();
        else if(lookaheadToken == '*' || lookaheadToken == '/'){
            consume(lookaheadToken);
            int number = Factor();
            if (lookaheadToken == '*') result = number*input;
            if (lookaheadToken == '/') result = number/input;
            returnResult = Term2(result);
        }
        return returnResult;
    }

    private int Factor() throws IOException, ParseError{
        // System.out.printf("Factor: lookaheadToken: [%c]\n", lookaheadToken);
        if(lookaheadToken >= '0' && lookaheadToken <= '9') return Num1();
        else if(lookaheadToken == '(') {
            consume(lookaheadToken);
            Exp1();
            consume(')');
        }
        else throw new ParseError();
        return -1;
    }

    private int Num1() throws IOException, ParseError{
        // System.out.printf("Num1: lookaheadToken: [%c]\n", lookaheadToken);
        String number="";
        if(lookaheadToken < '0' || lookaheadToken > '9') throw new ParseError();
        else {
            char digit = Digit();
            number=number+digit;
            String returnNum = Num2(number);
            if(returnNum!="") {
                number=number+returnNum;
                int result = Integer.parseInt(number);
                return result;
            }
        }
        return -1;
    }

    private String Num2(String input) throws IOException, ParseError{
        // System.out.printf("Num2: lookaheadToken: [%c]\n", lookaheadToken);
        x = lookaheadToken;
        String number=input;
        if(lookaheadToken >= '0' && lookaheadToken <= '9') {
            char digit = Digit();
            number=number+digit;
            System.out.printf("Num2: here: [%s]\n", number);
            Num2(number);
        }
        // if(x=='+'||x=='-'||x=='/'||x=='*'||x==')'||x=='\n'||x==-1||x=='\r') return number;
        else throw new ParseError();
        return number;
    }

    private char Digit() throws IOException, ParseError{
        // System.out.printf("Digit: lookaheadToken: [%c]\n", lookaheadToken);
        char returned = (char)lookaheadToken;
        if(lookaheadToken < '0' || lookaheadToken > '9') throw new ParseError();
        else consume(lookaheadToken);
        return returned;
    }

    public void parse() throws IOException, ParseError{
        int result = Exp1();
        System.out.printf("=%d\n", result);
        if (lookaheadToken != '\n' && lookaheadToken != -1 && lookaheadToken != '\r')
            throw new ParseError();
    }

    public static void main(String[] args){
        try{
            CalculatorEval parser = new CalculatorEval(System.in);
            parser.parse();
        }
        catch(IOException e){ System.err.println(e.getMessage()); }
        catch(ParseError err){ System.err.println(err.getMessage()); }
    }
}
