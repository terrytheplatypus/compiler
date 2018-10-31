/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package X0;

import X0.*;
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
public class X0Interpreter {
    
    public static int argEvaluate(X0Arg a, Map <String, Integer> varMap) {
        if(a instanceof X0Int) {
            return ((X0Int) a).getVal();
        } else if(a instanceof X0RegWithOffset) {
            //use a list or queue (or stack) to represent the stack
        } else if (a instanceof X0Reg) {
            //use a map to represent the registers
//            return rax;
        } else if(a instanceof X0ByteReg) {
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
       
       public static String X0Interpret(X0TypedProgram p) {
           if(p.getType() == int.class) {
               return String.valueOf(X0Interpret(p.getProg()));
           } else if(p.getType() == boolean.class) {
               return X0Interpret(p.getProg()) == 1?"true":"false";
           }
           return "error matching type";
       }
       
    public static int X0Interpret(X0Program p){
        
        //initially populate <var name, int> map with var names and integer min value
        Map <String, Integer> varValues = new HashMap<>();
//        for(X0Var v:p.getVarList())
//            varValues.put(v.getName(), Integer.MIN_VALUE);

        //iterate thru all statements and update map accordingly
        
        
//        List <C0Stmt> progStmts = p.getStmtList();
//        ListIterator <C0Stmt> iter = progStmts.listIterator();
        

        //you get the copy because the implementation modifies the given
        //statement list, and you wouldn't want to do that with the actual statement list
        List <X0Instr> progInstrs = p.getInstrListCopy();
        
         ListIterator <X0Instr> iter = progInstrs.listIterator();
        while(iter.hasNext()) {
            X0Instr i = iter.next();
            if(i instanceof X0movq) {
                int n = argEvaluate(((X0movq) i).getA(),varValues);
                String varName = ((X0Var)((X0movq) i).getB()).getName();
                varValues.put(varName, n);
            } else if(i instanceof X0callq) {
                if ("readint".equals(((X0callq) i).getLabel())) {
                    System.out.println("Enter number");
                    rax = (new Scanner(System.in)).nextInt();
                }
                
            } else if(i instanceof X0retq) {
                return argEvaluate(((X0retq) i).getX(),varValues);
            } else if(i instanceof X0negq) {
                int k = -argEvaluate(((X0negq) i).getX(), varValues);
                //getting name of variable to stick it in the map
                varValues.put(( (X0Var) ((X0negq) i).getX()).getName(), k);
            } else if(i instanceof X0addq) {
                int sum = argEvaluate(((X0addq) i).getA(), varValues) +
                        argEvaluate(((X0addq) i).getB(), varValues);
                varValues.put(( (X0Var) ((X0addq) i).getB()).getName(), sum);
            }
            
            //instrs added with conditional pass:
            else if (i instanceof X0xorq) {
                int result = argEvaluate(((X0xorq) i).getA(), varValues) ^
                        argEvaluate(((X0xorq) i).getB(), varValues);
                
                varValues.put(( (X0Var) ((X0xorq) i).getB()).getName(), result);
            } else if(i instanceof  X0cmpq) {
                //cmpq is subtraction without modification of arguments
                int diff = argEvaluate(((X0cmpq) i).getB(), varValues) -
                        argEvaluate(((X0cmpq) i).getA(), varValues);
                EFLAGS = diff;
            } else if( i instanceof X0set) {
                X1.X1set.conditionCode cc = ((X0set) i).getCc();
                
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
            } else if(i instanceof X0movzbq) {
                //assuming i'm only going to use movzbq with al and a var
                String varName = ((X0Var)((X0movzbq) i).getB()).getName();
                varValues.put(varName, al);
            } else if (i instanceof X0If) {
                //check if given var is 1 or 0, add corresponding branch's
                //statement list to the statement list
                //you can assume it's a var because if condition in flatten
                //compares result of actual comparison with  true
                X0Var v =  (X0Var)((X0If) i).getCond();
                boolean flag = argEvaluate(v,varValues)== 1;
                if(flag) {
                    
                    for(X0Instr curr:((X0If) i).getIfs()) {
                        iter.add(curr);
                    }
                    
                    for(X0Instr curr:((X0If) i).getIfs()) {
                        iter.previous();
                    }
                    
                } else {
                    for(X0Instr curr:((X0If) i).getElses()) {
                        iter.add(curr);
                    }
                    
                    for(X0Instr curr:((X0If) i).getElses()) {
                        iter.previous();
                    }
                }
            }
        }
        
        return Integer.MIN_VALUE;
    }
    public static String X0PrintWithLiveAfters (X0Program x) {
         return X0PrintWithLiveAfters (x , 0);
    }
    
    /**
     * 
     * @param x
     * @param depth is the depth of if statements
     */
    public static String X0PrintWithLiveAfters (X0Program x , int depth) {
        String prog = "";
        int n = 1;
        int m = 0;
        String indent;
        if(depth > 0) indent =  String.format("%1$" + depth+"s","");
        else indent = "";
        for(X0Instr i:x.getInstrList()) {
            //n++ and the string concatenation +
            prog+= n+++":  ";
            prog += indent;
            if(i instanceof X0movq) {
                prog+="movq "+ StringifyX0Arg(((X0movq) i).getA())
                        +" "+ StringifyX0Arg(((X0movq) i).getB());
            } else if(i instanceof X0callq) {
                prog+= "callq "+((X0callq) i).getLabel();
            } else if(i instanceof X0retq) {
                prog+= "retq "+StringifyX0Arg(((X0retq) i).getX());
            } else if(i instanceof X0negq) {
                prog += "negq "+StringifyX0Arg(((X0negq) i).getX());
            } else if(i instanceof X0addq) {
                prog+="addq "+ StringifyX0Arg(((X0addq) i).getA())
                        +" "+ StringifyX0Arg(((X0addq) i).getB());
            } else if(i instanceof X0cmpq) {
                prog +="cmpq " + StringifyX0Arg(((X0cmpq) i).getA())
                        +" " + StringifyX0Arg(((X0cmpq) i).getB());
            } else if(i instanceof X0xorq) {
                prog += "xorq " + StringifyX0Arg(((X0xorq) i).getA())
                        +" " + StringifyX0Arg(((X0xorq) i).getB());
            } else if (i instanceof X0set) {
                prog += "set "+ StringifyCC(((X0set) i).getCc())
                        +" " + StringifyX0Arg(((X0set) i).getA());
            } else if (i instanceof X0movzbq) {
                prog+="movzbq "+ StringifyX0Arg(((X0movzbq) i).getA())
                        +" "+ StringifyX0Arg(((X0movzbq) i).getB());
            }
            else if (i instanceof X0If) {
                X0If i2 = (X0If) i;
                X0Var condV = (X0Var) ((X0If) i).getCond();
                prog +="if ("+ StringifyX0Arg(condV) +" == 1 ) {\n";
                //passes an empty list because that var list is not used
                X0Program ifBranch = new X0Program(new ArrayList <>(),
                                                i2.getIfs(),
                                                new X0Var("_"));
                ifBranch.setLiveAfters(((X0If) i).getIfLivAfs());
                
                X0Program elseBranch = new X0Program(new ArrayList <>(),
                                                i2.getElses(),
                                                new X0Var("_"));
                elseBranch.setLiveAfters(((X0If) i).getElseLivAfs());
                
                String ifString = X0PrintWithLiveAfters(ifBranch, depth+1);
                String elseString = X0PrintWithLiveAfters(elseBranch, depth+1);
                prog+= ifString + indent +"}\n";
                prog += indent+"else {\n"+elseString+indent+"}";
            }
            
            if(x.getLiveAfters()!= null) {
                prog+="[";
                if(x.getLiveAfters().get(n-2) == null) { prog+="}\n";continue;}
                for(X0Var o:x.getLiveAfters().get(n-2)) {
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
    private static String StringifyX0Arg(X0Arg a) {
        if(a instanceof X0Int) {
            return String.valueOf(((X0Int) a).getVal());
        } else if(a instanceof X0Var) {
            return ((X0Var) a).getName();
        } else if (a instanceof X0Reg) {
            return "rax";
        } else if (a instanceof X0ByteReg) {
            return "al";
        }
        return
                "";
    }
}
