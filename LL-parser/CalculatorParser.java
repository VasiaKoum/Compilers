import java.io.InputStream;
import java.io.IOException;

class CalculatorParser{
    private int lookaheadToken, x;
    private InputStream in;

    public CalculatorParser(InputStream in) throws IOException{
        this.in = in;
        lookaheadToken = in.read();
    }
    private void consume(int symbol) throws IOException, ParseError{
        if (lookaheadToken != symbol)
            throw new ParseError();
        lookaheadToken = in.read();
    }

    private void Exp1() throws IOException, ParseError{
        // System.out.printf("Exp1: lookaheadToken: [%c]\n", lookaheadToken);
        if((lookaheadToken >= '0' && lookaheadToken <= '9') || lookaheadToken == '('){
            Term1(); Exp2();
        }
        else throw new ParseError();
    }

    private void Exp2() throws IOException, ParseError{
        // System.out.printf("Exp2: lookaheadToken: [%c]\n", lookaheadToken);
        if(lookaheadToken == '\n' || lookaheadToken == -1 || lookaheadToken == '\r' || lookaheadToken == ')')
            return;
        else if(lookaheadToken == '+' || lookaheadToken == '-'){
            consume(lookaheadToken);
            Term1(); Exp2();
        }
        else throw new ParseError();
    }

    private void Term1() throws IOException, ParseError{
        // System.out.printf("Term1: lookaheadToken: [%c]\n", lookaheadToken);
        if((lookaheadToken >= '0' && lookaheadToken <= '9') || lookaheadToken == '('){
            Factor(); Term2();
        }
        else throw new ParseError();

    }

    private void Term2() throws IOException, ParseError{
        // System.out.printf("Term2: lookaheadToken: [%c]\n", lookaheadToken);
        x = lookaheadToken;
        if((lookaheadToken >= '0' && lookaheadToken <= '9') || lookaheadToken == '(') throw new ParseError();
        else if(lookaheadToken == '*' || lookaheadToken == '/'){
            consume(lookaheadToken);
            Factor(); Term2();
        }
        else if(x=='+'||x=='-'||x==')'||x=='\n'||x==-1||x=='\r')
            return;
    }

    private void Factor() throws IOException, ParseError{
        // System.out.printf("Factor: lookaheadToken: [%c]\n", lookaheadToken);
        if(lookaheadToken >= '0' && lookaheadToken <= '9') Num1();
        else if(lookaheadToken == '(') {
            consume(lookaheadToken);
            Exp1();
            consume(')');
        }
        else throw new ParseError();
    }

    private void Num1() throws IOException, ParseError{
        // System.out.printf("Num1: lookaheadToken: [%c]\n", lookaheadToken);
        if(lookaheadToken < '0' || lookaheadToken > '9') throw new ParseError();
        else { Digit(); Num2(); }
    }

    private void Num2() throws IOException, ParseError{
        // System.out.printf("Num2: lookaheadToken: [%c]\n", lookaheadToken);
        x = lookaheadToken;
        if(lookaheadToken >= '0' && lookaheadToken <= '9') { Digit(); Num2();}
        if(x=='+'||x=='-'||x=='/'||x=='*'||x==')'||x=='\n'||x==-1||x=='\r')
            return;
        else throw new ParseError();

    }

    private void Digit() throws IOException, ParseError{
        // System.out.printf("Digit: lookaheadToken: [%c]\n", lookaheadToken);
        if(lookaheadToken < '0' || lookaheadToken > '9') throw new ParseError();
        else consume(lookaheadToken);
    }

    public void parse() throws IOException, ParseError{
        Exp1();
        if (lookaheadToken != '\n' && lookaheadToken != -1 && lookaheadToken != '\r')
            throw new ParseError();
    }

    public static void main(String[] args){
        try{
            CalculatorParser parser = new CalculatorParser(System.in);
            parser.parse();
        }
        catch(IOException e){ System.err.println(e.getMessage()); }
        catch(ParseError err){ System.err.println(err.getMessage()); }
    }
}
