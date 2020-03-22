import java.io.InputStream;
import java.io.IOException;

class CalculatorParser{
    private int lookaheadToken, check_digit;
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
        System.out.printf("Exp1: lookaheadToken: [%c]\n", lookaheadToken);
        if (lookaheadToken == '\n' || lookaheadToken == -1 || lookaheadToken == '\r')
            throw new ParseError();
        Term1(); Exp2();
    }

    private void Exp2() throws IOException, ParseError{
        if(lookaheadToken == '\n' || lookaheadToken == -1 || lookaheadToken != '\r')
    	    return;
    	if(lookaheadToken == '+'){
            consume('+');
            Term1(); Exp2();
            return;
        }
    	if(lookaheadToken == '-'){
            consume('-');
            Term1(); Exp2();
            return;
        }

    }

    private void Num() throws IOException, ParseError{
        check_digit = in.read();
        if(check_digit >= '0' && check_digit <= '9'){
            lookaheadToken = check_digit;
            Digit(); Num();
        }
        else{
            if(lookaheadToken >= '0' && lookaheadToken <= '9') Digit();
            else throw new ParseError();
        }
    }

    private void Digit() throws IOException, ParseError{
        if(lookaheadToken < '0' || lookaheadToken > '9')
            throw new ParseError();
        consume(lookaheadToken);
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
