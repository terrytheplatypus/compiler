/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package X1;

import X1.X1set.conditionCode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author david
 */
public class X1Interpreter {
    
    public static int argEvaluate(X1Arg a, Map <String, Integer> varMap) {
        if(a instanceof X1Int) {
            return ((X1Int) a).getVal();
        } else if(a instanceof X1Var) {
            return varMap.get(((X1Var) a).getName());
        } else if (a instanceof X1Reg) {
            return rax;
        } else if(a instanceof X1ByteReg) {
            return al;
        }
        return 0;
    }
    
    //holds rax and register values
       private static int rax;
       private static int al;
       private static int EFLAGS;
    
    //this assumes that the 2nd spot in a movq and addq and the only spot in negq are variables.
    // this is not in the language definition, but the flatten step makes this an invariant
    //similar thing for addq
       
       public static String X1Interpret(X1TypedProgram p) {
           if(p.getType() == int.class) {
               return String.valueOf(X1Interpret(p.getProg()));
           } else if(p.getType() == boolean.class) {
               return X1Interpret(p.getProg()) == 1?"true":"false";
           }
           return "error matching type";
       }
       
    public static int X1Interpret(X1Program p){
        
        //initially populate <var name, int> map with var names and integer min value
        Map <String, Integer> varValues = new HashMap<>();
        for(X1Var v:p.getVarList())
            varValues.put(v.getName(), Integer.MIN_VALUE);
        //iterate thru all statements and update map accordingly
        
        
//        List <C0Stmt> progStmts = p.getStmtList();
//        ListIterator <C0Stmt> iter = progStmts.listIterator();
        

        //you get the copy because the implementation modifies the given
        //statement list, and you wouldn't want to do that with the actual statement list
        List <X1Instr> progInstrs = p.getInstrListCopy();
        
         ListIterator <X1Instr> iter = progInstrs.listIterator();
        while(iter.hasNext()) {
            X1Instr i = iter.next();
            if(i instanceof X1movq) {
                int n = argEvaluate(((X1movq) i).getA(),varValues);
                String varName = ((X1Var)((X1movq) i).getB()).getName();
                varValues.put(varName, n);
            } else if(i instanceof X1callq) {
                if ("readint".equals(((X1callq) i).getLabel())) {
                    System.out.println("Enter number");
                    rax = (new Scanner(System.in)).nextInt();
                }
                
            } else if(i instanceof X1retq) {
                return argEvaluate(((X1retq) i).getX(),varValues);
            } else if(i instanceof X1negq) {
                int k = -argEvaluate(((X1negq) i).getX(), varValues);
                //getting name of variable to stick it in the map
                varValues.put(( (X1Var) ((X1negq) i).getX()).getName(), k);
            } else if(i instanceof X1addq) {
                int sum = argEvaluate(((X1addq) i).getA(), varValues) +
                        argEvaluate(((X1addq) i).getB(), varValues);
                varValues.put(( (X1Var) ((X1addq) i).getB()).getName(), sum);
            }
            
            //instrs added with conditional pass:
            else if (i instanceof X1xorq) {
                int result = argEvaluate(((X1xorq) i).getA(), varValues) ^
                        argEvaluate(((X1xorq) i).getB(), varValues);
                
                varValues.put(( (X1Var) ((X1xorq) i).getB()).getName(), result);
            } else if(i instanceof  X1cmpq) {
                //cmpq is subtraction without modification of arguments
                int diff = argEvaluate(((X1cmpq) i).getB(), varValues) -
                        argEvaluate(((X1cmpq) i).getA(), varValues);
                EFLAGS = diff;
            } else if( i instanceof X1set) {
                X1set.conditionCode cc = ((X1set) i).getCc();
                
                switch (cc) {
                    case e:
                        al = (EFLAGS == 0 )?1:0;
                        break;
                    case g:
                        al = (EFLAGS > 0 )?1:0;
                        break;
                    case ge:
                        al = (EFLAGS >= 0 )?1:0;
                        break;
                    case l:
                        al = (EFLAGS< 0 )?1:0;
                        break;
                    case le:
                        al = (EFLAGS <= 0 )?1:0;
                        break;
                            
                }
            } else if(i instanceof X1movzbq) {
                //assuming i'm only going to use movzbq with al and a var
                String varName = ((X1Var)((X1movzbq) i).getB()).getName();
                varValues.put(varName, al);
            } else if (i instanceof X1If) {
                //check if given var is 1 or 0, add corresponding branch's
                //statement list to the statement list
                //you can assume it's a var because if condition in flatten
                //compares result of actual comparison with  true
                X1Var v =  (X1Var)((X1If) i).getCond();
                boolean flag = argEvaluate(v,varValues)== 1;
                if(flag) {
                    
                    for(X1Instr curr:((X1If) i).getIfs()) {
                        iter.add(curr);
                    }
                    
                    for(X1Instr curr:((X1If) i).getIfs()) {
                        iter.previous();
                    }
                    
                } else {
                    for(X1Instr curr:((X1If) i).getElses()) {
                        iter.add(curr);
                    }
                    
                    for(X1Instr curr:((X1If) i).getElses()) {
                        iter.previous();
                    }
                }
            }
        }
        
        return Integer.MIN_VALUE;
    }
    public static String X1PrintWithLiveAfters (X1Program x) {
         return X1PrintWithLiveAfters (x , 0);
    }
    
    /**
     * 
     * @param x
     * @param depth is the depth of if statements
     */
    public static String X1PrintWithLiveAfters (X1Program x , int depth) {
        String prog = "";
        int n = 1;
        int m = 0;
        String indent;
        if(depth > 0) indent =  String.format("%1$" + depth+"s","");
        else indent = "";
        for(X1Instr i:x.getInstrList()) {
            //n++ and the string concatenation +
            prog+= n+++":  ";
            prog += indent;
            if(i instanceof X1movq) {
                prog+="movq "+ StringifyX1Arg(((X1movq) i).getA())
                        +" "+ StringifyX1Arg(((X1movq) i).getB());
            } else if(i instanceof X1callq) {
                prog+= "callq "+((X1callq) i).getLabel();
            } else if(i instanceof X1retq) {
                prog+= "retq "+StringifyX1Arg(((X1retq) i).getX());
            } else if(i instanceof X1negq) {
                prog += "negq "+StringifyX1Arg(((X1negq) i).getX());
            } else if(i instanceof X1addq) {
                prog+="addq "+ StringifyX1Arg(((X1addq) i).getA())
                        +" "+ StringifyX1Arg(((X1addq) i).getB());
            } else if(i instanceof X1cmpq) {
                prog +="cmpq " + StringifyX1Arg(((X1cmpq) i).getA())
                        +" " + StringifyX1Arg(((X1cmpq) i).getB());
            } else if(i instanceof X1xorq) {
                prog += "xorq " + StringifyX1Arg(((X1xorq) i).getA())
                        +" " + StringifyX1Arg(((X1xorq) i).getB());
            } else if (i instanceof X1set) {
                prog += "set "+ StringifyCC(((X1set) i).getCc())
                        +" " + StringifyX1Arg(((X1set) i).getA());
            } else if (i instanceof X1movzbq) {
                prog+="movzbq "+ StringifyX1Arg(((X1movzbq) i).getA())
                        +" "+ StringifyX1Arg(((X1movzbq) i).getB());
            }
            else if (i instanceof X1If) {
                X1If i2 = (X1If) i;
                X1Var condV = (X1Var) ((X1If) i).getCond();
                prog +="if ("+ StringifyX1Arg(condV) +" == 1 ) {\n";
                //passes an empty list because that var list is not used
                X1Program ifBranch = new X1Program(new ArrayList <>(),
                                                i2.getIfs(),
                                                new X1Var("_"));
                ifBranch.setLiveAfters(((X1If) i).getIfLivAfs());
                
                X1Program elseBranch = new X1Program(new ArrayList <>(),
                                                i2.getElses(),
                                                new X1Var("_"));
                elseBranch.setLiveAfters(((X1If) i).getElseLivAfs());
                
                String ifString = X1PrintWithLiveAfters(ifBranch, depth+1);
                String elseString = X1PrintWithLiveAfters(elseBranch, depth+1);
                prog+= ifString + indent +"}\n";
                prog += indent+"else {\n"+elseString+indent+"}";
            }
            
            if(x.getLiveAfters()!= null) {
                prog+="[";
                if(x.getLiveAfters().get(n-2) == null) { prog+="}\n";continue;}
                for(X1Var o:x.getLiveAfters().get(n-2)) {
                    prog+= o.getName() + ", ";
                } 
                prog += "]";
            } else prog += "no live-after set for this instr";
            prog += "\n";
            //m++;
        }
        //System.out.println(prog);
        return prog;
        
    }
    
    public static String StringifyCC(conditionCode cc) {
        switch (cc) {
                    case e:
                        return "=";
                    case g:
                        return ">";
                    case ge:
                        return ">=";
                    case l:
                        return "<";
                    case le:
                        return "<=";
        }
        return "error";
    }
    private static String StringifyX1Arg(X1Arg a) {
        if(a instanceof X1Int) {
            return String.valueOf(((X1Int) a).getVal());
        } else if(a instanceof X1Var) {
            return ((X1Var) a).getName();
        } else if (a instanceof X1Reg) {
            return "rax";
        } else if (a instanceof X1ByteReg) {
            return "al";
        }
        return
                "";
    }
}
