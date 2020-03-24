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
        int result=-1;
        if((lookaheadToken >= '0' && lookaheadToken <= '9') || lookaheadToken == '('){
            int number = Term1();
            result = Exp2(number);
        }
        else throw new ParseError();
        return result;
    }

    private int Exp2(int input) throws IOException, ParseError{
        int returnResult=-1, result=0;
        if(lookaheadToken == '+' || lookaheadToken == '-'){
            int op = lookaheadToken;
            consume(lookaheadToken);
            int number = Term1();
            if (op == '+') result = input+number;
            if (op == '-') result = input-number;
            returnResult = Exp2(result);
        }
        else if(!(lookaheadToken == '\n' || lookaheadToken == -1 || lookaheadToken == '\r' || lookaheadToken == ')'))
            throw new ParseError();
        else returnResult = input;
        return returnResult;
    }

    private int Term1() throws IOException, ParseError{
        int result = -1;
        if((lookaheadToken >= '0' && lookaheadToken <= '9') || lookaheadToken == '('){
            int number = Factor();
            result = Term2(number);
        }
        else throw new ParseError();
        return result;
    }

    private int Term2(int input) throws IOException, ParseError{
        x = lookaheadToken;
        int returnResult=-1, result=0;
        if((lookaheadToken >= '0' && lookaheadToken <= '9') || lookaheadToken == '(') throw new ParseError();
        else if(lookaheadToken == '*' || lookaheadToken == '/'){
            int op = lookaheadToken;
            consume(lookaheadToken);
            int number = Factor();
            if (op == '*') result = input*number;
            if (op == '/') result = input/number;
            returnResult = Term2(result);
        }
        else returnResult = input;
        return returnResult;
    }

    private int Factor() throws IOException, ParseError{
        int number=0;
        if(lookaheadToken >= '0' && lookaheadToken <= '9') number = Num1();
        else if(lookaheadToken == '(') {
            consume(lookaheadToken);
            number = Exp1();
            consume(')');

        }
        else throw new ParseError();
        return number;
    }

    private int Num1() throws IOException, ParseError{
        String number="";
        if(lookaheadToken < '0' || lookaheadToken > '9') throw new ParseError();
        else {
            char digit = Digit();
            number=number+digit;
            String returnNum = Num2(number);
            if(returnNum!="") {
                int result = Integer.parseInt(returnNum);
                return result;
            }
        }
        return -1;
    }

    private String Num2(String input) throws IOException, ParseError{
        String number=input;
        if(lookaheadToken >= '0' && lookaheadToken <= '9') {
            char digit = Digit();
            number=number+digit;
            Num2(number);
        }
        x = lookaheadToken;
        if(!(x=='+'||x=='-'||x=='/'||x=='*'||x==')'||x=='\n'||x==-1||x=='\r'))
            throw new ParseError();
        return number;
    }

    private char Digit() throws IOException, ParseError{
        char returned = (char)lookaheadToken;
        if(lookaheadToken < '0' || lookaheadToken > '9') throw new ParseError();
        else consume(lookaheadToken);
        return returned;
    }

    public void parse() throws IOException, ParseError{
        int result = Exp1();
        if (lookaheadToken != '\n' && lookaheadToken != -1 && lookaheadToken != '\r')
            throw new ParseError();
        System.out.printf("=%d\n", result);
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
