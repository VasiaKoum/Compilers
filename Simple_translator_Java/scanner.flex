import java_cup.runtime.*;
%%
/* ----------------- Options and Declarations Section----------------- */
%class Scanner
%line
%column
%cup

%{
StringBuffer stringBuffer = new StringBuffer();
private Symbol symbol(int type) {
   return new Symbol(type, yyline, yycolumn);
}
private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline, yycolumn, value);
}
%}

Ident = [a-zA-Z$_] [a-zA-Z0-9$_]*
BoolCond = true | false
LineTerminator = \r|\n|\r\n
WhiteSpace     = {LineTerminator} | [ \t\f]

%state STRING
%%
/* ------------------------Lexical Rules Section---------------------- */
<YYINITIAL> {
 "+"            { return symbol(sym.PLUS); }
 "("            { return symbol(sym.LPAREN); }
 ")"            { return symbol(sym.RPAREN); }
 "{"            { return symbol(sym.LBRACK); }
 "}"            { return symbol(sym.RBRACK); }
 ","            { return symbol(sym.COMMA); }
 "if"           { return symbol(sym.IF); }
 "else"         { return symbol(sym.ELSE); }
 "prefix"       { return symbol(sym.PREFIX); }
 "reverse"      { return symbol(sym.REVERSE); }
 \"             { stringBuffer.setLength(0); yybegin(STRING); }
 {Ident}        { return symbol(sym.IDENT, new String(yytext())); }
 {BoolCond}     { return symbol(sym.BOOLCOND); }
 {WhiteSpace}   { /* just skip what was found, do nothing */ }
}

<STRING> {
      \"                             { yybegin(YYINITIAL);
                                       return symbol(sym.STRING_LITERAL, stringBuffer.toString()); }
      [^\n\r\"\\]+                   { stringBuffer.append( yytext() ); }
      \\t                            { stringBuffer.append('\t'); }
      \\n                            { stringBuffer.append('\n'); }

      \\r                            { stringBuffer.append('\r'); }
      \\\"                           { stringBuffer.append('\"'); }
      \\                             { stringBuffer.append('\\'); }
}

/* No token was found for the input so through an error.  Print out an
   Illegal character message with the illegal character that was found. */
[^]                    { throw new Error("Illegal character <"+yytext()+">"); }
