
//----------------------------------------------------
// The following code was generated by CUP v0.11b 20141202 (SVN rev 60)
//----------------------------------------------------

import java_cup.runtime.*;
import java_cup.runtime.XMLElement;

/** CUP v0.11b 20141202 (SVN rev 60) generated parser.
  */
@SuppressWarnings({"rawtypes"})
public class Parser extends java_cup.runtime.lr_parser {

 public final Class getSymbolContainer() {
    return sym.class;
}

  /** Default constructor. */
  public Parser() {super();}

  /** Constructor which sets the default scanner. */
  public Parser(java_cup.runtime.Scanner s) {super(s);}

  /** Constructor which sets the default scanner. */
  public Parser(java_cup.runtime.Scanner s, java_cup.runtime.SymbolFactory sf) {super(s,sf);}

  /** Production table. */
  protected static final short _production_table[][] = 
    unpackFromStrings(new String[] {
    "\000\013\000\002\002\003\000\002\002\004\000\002\003" +
    "\004\000\002\003\003\000\002\004\006\000\002\005\006" +
    "\000\002\007\005\000\002\007\003\000\002\007\002\000" +
    "\002\006\003\000\002\010\003" });

  /** Access to production table. */
  public short[][] production_table() {return _production_table;}

  /** Parse-action table. */
  protected static final short[][] _action_table = 
    unpackFromStrings(new String[] {
    "\000\023\000\004\016\007\001\002\000\004\007\021\001" +
    "\002\000\004\002\020\001\002\000\006\002\ufffe\016\ufffe" +
    "\001\002\000\004\005\012\001\002\000\006\002\001\016" +
    "\007\001\002\000\006\002\uffff\016\uffff\001\002\000\010" +
    "\006\ufff9\011\ufff9\016\014\001\002\000\006\006\016\011" +
    "\015\001\002\000\006\006\ufffa\011\ufffa\001\002\000\004" +
    "\016\017\001\002\000\004\007\ufffc\001\002\000\006\006" +
    "\ufffb\011\ufffb\001\002\000\004\002\000\001\002\000\004" +
    "\017\024\001\002\000\004\010\025\001\002\000\004\010" +
    "\ufff8\001\002\000\004\010\ufff7\001\002\000\006\002\ufffd" +
    "\016\ufffd\001\002" });

  /** Access to parse-action table. */
  public short[][] action_table() {return _action_table;}

  /** <code>reduce_goto</code> table. */
  protected static final short[][] _reduce_table = 
    unpackFromStrings(new String[] {
    "\000\023\000\012\002\004\003\007\004\005\005\003\001" +
    "\001\000\002\001\001\000\002\001\001\000\002\001\001" +
    "\000\002\001\001\000\006\004\010\005\003\001\001\000" +
    "\002\001\001\000\004\007\012\001\001\000\002\001\001" +
    "\000\002\001\001\000\002\001\001\000\002\001\001\000" +
    "\002\001\001\000\002\001\001\000\006\006\021\010\022" +
    "\001\001\000\002\001\001\000\002\001\001\000\002\001" +
    "\001\000\002\001\001" });

  /** Access to <code>reduce_goto</code> table. */
  public short[][] reduce_table() {return _reduce_table;}

  /** Instance of action encapsulation class. */
  protected CUP$Parser$actions action_obj;

  /** Action encapsulation object initializer. */
  protected void init_actions()
    {
      action_obj = new CUP$Parser$actions(this);
    }

  /** Invoke a user supplied parse action. */
  public java_cup.runtime.Symbol do_action(
    int                        act_num,
    java_cup.runtime.lr_parser parser,
    java.util.Stack            stack,
    int                        top)
    throws java.lang.Exception
  {
    /* call code in generated class */
    return action_obj.CUP$Parser$do_action(act_num, parser, stack, top);
  }

  /** Indicates start state. */
  public int start_state() {return 0;}
  /** Indicates start production. */
  public int start_production() {return 1;}

  /** <code>EOF</code> Symbol index. */
  public int EOF_sym() {return 0;}

  /** <code>error</code> Symbol index. */
  public int error_sym() {return 1;}


  /** Scan to get the next Symbol. */
  public java_cup.runtime.Symbol scan()
    throws java.lang.Exception
    {
 return s.next_token(); 
    }


    Scanner s;
    Parser(Scanner s){ this.s=s; }


/** Cup generated class to encapsulate user supplied action code.*/
@SuppressWarnings({"rawtypes", "unchecked", "unused"})
class CUP$Parser$actions {
  private final Parser parser;

  /** Constructor */
  CUP$Parser$actions(Parser parser) {
    this.parser = parser;
  }

  /** Method 0 with the actual generated action code for actions 0 to 300. */
  public final java_cup.runtime.Symbol CUP$Parser$do_action_part00000000(
    int                        CUP$Parser$act_num,
    java_cup.runtime.lr_parser CUP$Parser$parser,
    java.util.Stack            CUP$Parser$stack,
    int                        CUP$Parser$top)
    throws java.lang.Exception
    {
      /* Symbol object for return from actions */
      java_cup.runtime.Symbol CUP$Parser$result;

      /* select the action based on the action number */
      switch (CUP$Parser$act_num)
        {
          /*. . . . . . . . . . . . . . . . . . . .*/
          case 0: // begin ::= fdecl 
            {
              Object RESULT =null;
		int fdleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).left;
		int fdright = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).right;
		Object fd = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.peek()).value;
		 System.out.println("public static void main(String[] args){\n"+" "+"\n}\n");
                                                    System.out.println(fd);
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("begin",0, ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 1: // $START ::= begin EOF 
            {
              Object RESULT =null;
		int start_valleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).left;
		int start_valright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).right;
		Object start_val = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-1)).value;
		RESULT = start_val;
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("$START",0, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          /* ACCEPT */
          CUP$Parser$parser.done_parsing();
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 2: // fdecl ::= fdecl func 
            {
              Object RESULT =null;
		int fd2left = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).left;
		int fd2right = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).right;
		Object fd2 = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-1)).value;
		int fleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).left;
		int fright = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).right;
		Object f = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.peek()).value;
		 RESULT = fd2+"\n\n"+f; 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("fdecl",1, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 3: // fdecl ::= func 
            {
              Object RESULT =null;
		int f2left = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).left;
		int f2right = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).right;
		Object f2 = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.peek()).value;
		 RESULT = f2; 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("fdecl",1, ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 4: // func ::= name LBRACK body RBRACK 
            {
              Object RESULT =null;
		int nleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-3)).left;
		int nright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-3)).right;
		Object n = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-3)).value;
		int bleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).left;
		int bright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).right;
		Object b = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-1)).value;
		 RESULT = "public static String "+n+"{\n"+b+"\n}"; 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("func",2, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-3)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 5: // name ::= IDENT LPAREN args RPAREN 
            {
              Object RESULT =null;
		int ileft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-3)).left;
		int iright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-3)).right;
		Object i = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-3)).value;
		int aleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).left;
		int aright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).right;
		Object a = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-1)).value;
		 RESULT = i+"("+a+")"; 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("name",3, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-3)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 6: // args ::= args COMMA IDENT 
            {
              Object RESULT =null;
		int aleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)).left;
		int aright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)).right;
		Object a = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-2)).value;
		int ileft = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).left;
		int iright = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).right;
		Object i = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.peek()).value;
		 RESULT = a+", "+i; 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("args",5, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 7: // args ::= IDENT 
            {
              Object RESULT =null;
		int ileft = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).left;
		int iright = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).right;
		Object i = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.peek()).value;
		 RESULT = i; 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("args",5, ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 8: // args ::= 
            {
              Object RESULT =null;
		 RESULT = ""; 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("args",5, ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 9: // body ::= returned 
            {
              Object RESULT =null;
		int rleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).left;
		int rright = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).right;
		Object r = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.peek()).value;
		 RESULT = "\treturn "+r+";"; 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("body",4, ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 10: // returned ::= STRING_LITERAL 
            {
              Object RESULT =null;
		int sleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).left;
		int sright = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).right;
		String s = (String)((java_cup.runtime.Symbol) CUP$Parser$stack.peek()).value;
		 RESULT = "\""+s+"\""; 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("returned",6, ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /* . . . . . .*/
          default:
            throw new Exception(
               "Invalid action number "+CUP$Parser$act_num+"found in internal parse table");

        }
    } /* end of method */

  /** Method splitting the generated action code into several parts. */
  public final java_cup.runtime.Symbol CUP$Parser$do_action(
    int                        CUP$Parser$act_num,
    java_cup.runtime.lr_parser CUP$Parser$parser,
    java.util.Stack            CUP$Parser$stack,
    int                        CUP$Parser$top)
    throws java.lang.Exception
    {
              return CUP$Parser$do_action_part00000000(
                               CUP$Parser$act_num,
                               CUP$Parser$parser,
                               CUP$Parser$stack,
                               CUP$Parser$top);
    }
}

}
