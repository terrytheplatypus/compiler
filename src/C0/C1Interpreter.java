/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package C0;

import C0.C0Cmp.opValue;
import R0.R0LitBool;
import R0.R0Var;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Scanner;
import javafx.util.Pair;

/**
 *
 * @author david
 */
public class C1Interpreter {
    
    
    //c1interpreter should return its return value and the variable map (used for if statements)
    
    
    private class VarMapper extends HashMap<String, C0Literal> {
        
    };
//    private class ReturnAndVarMap extends Pair<Integer, Integer> {
//        
//    };
    
    //this is to check that the flatten step gives the same answer
    //as the original expression

    //to evaluate it, you make a map from var names to ints and update it whenever stuff
    //is assigned, similarly to R0Interprter
    /*
    interpreter starts out by taking a C0 program, when it recurses through stuff it needs
    a map from String to Integer to keep track of the values of each variable
     */
    public static  C0Literal C1Interpret(C0Program p/*, VarMapper varMap*/) throws Exception {

        //initially populate <var name, int> map with var names and integer min value
        Map<String, C0Literal> varValues = new HashMap<>();
//        if(varMap != null)  {
//            varValues = varMap;
//        }
        for (C0Var v : p.getVarList()) {
            varValues.put(v.getName(), null);
        }
        //iterate thru all statements and update map accordingly
        List <C0Stmt> progStmts = p.getStmtList();
        ListIterator <C0Stmt> iter = progStmts.listIterator();
        while(iter.hasNext()) {
            C0Stmt s1 = iter.next();
            //most statements in a C0 program should still be assigns, 
            //so that should be checked first
            if (s1 instanceof C0Assign) {
                C0Assign s2 = (C0Assign) s1;
                C0Expression e = s2.getExp();
                String varName = s2.getX().getName();

                //rhs is int
                if (e instanceof C0Int) {
                    varValues.put(varName, ((C0Int) e));
                } else if (e instanceof C0LitBool) {
                    varValues.put(varName, ((C0LitBool) e));
                }
                //rhs is var
                else if (e instanceof C0Var) {
                    String var2 = ((C0Var) e).getName();
                    varValues.put(varName, varValues.get(var2));
                }
                //rhs is read
                else if (e instanceof C0Read) {
                    System.out.println("Enter a value:");
                    int n = new Scanner(System.in).nextInt();
                    varValues.put(varName, new C0Int(n));
                }
                //rhs is neg
                else if (e instanceof C0Neg) {
                    C0Arg a = ((C0Neg) e).getA();
                    if (a instanceof C0Int) {
                        varValues.put(varName, new C0Int(-((C0Int) a).getVal()));
                    } else if (a instanceof C0Var) {
                        String var2 = ((C0Var) a).getName();
                        C0Int val = (C0Int) varValues.get(var2);
                        varValues.put(varName, new C0Int(-val.getVal()));
                    }
                }
                //rhs is add
                else if (e instanceof C0Add) {
                    int first, second;
                    C0Arg a = ((C0Add) e).getA();
                    C0Arg b = ((C0Add) e).getB();
                    //in both cases, if it's not an int, it's a var
                    if (a instanceof C0Int) {
                        first = ((C0Int) a).getVal();
                    } else {
                        String var2 = ((C0Var) a).getName();
                        first = ((C0Int) varValues.get(var2)).getVal();
                    }
                    if (b instanceof C0Int) {
                        second = ((C0Int) b).getVal();
                    } else {
                        String var2 = ((C0Var) b).getName();
                        second = ((C0Int) varValues.get(var2)).getVal();
                    }
                    varValues.put(varName, new C0Int(first + second));
                } else if (e instanceof C0Not) {
                    C0Arg x = ((C0Not) e).getX();
                    boolean val;
                    //if x is a literal just return value
                    //if it's a var then get its value from the map
                    if (x instanceof R0LitBool) {
                        //C0LitBool b;
                        //b = ( varValues.get(var2)).getVal() ;
                        val = ((C0LitBool) x).getVal();
                        
                        
                    varValues.put(varName, new C0LitBool(val));
                        
                    } else if (x instanceof C0Var) {

                        C0LitBool b;
                        b = (C0LitBool) (varValues.get(((C0Var) x).getName()));
                        val = !b.getVal();
                        
                        
                    varValues.put(varName, new C0LitBool(val));

                    }
                    
                } else if(e instanceof C0Cmp) {
                    boolean result = evalC0Comp((C0Cmp)e, varValues);
                    varValues.put(varName, new C0LitBool(result));
                } else if (e instanceof C0And) {
                    boolean result;
                    C0And e2 = (C0And) e;
                    //next part is just in case I make some mistake and don't type check
                    if( (e2.getA() instanceof C0Int) || (e2.getB() instanceof C0Int) )
                        throw new Exception();
                    C0LitBool arg1 = (C0LitBool) convertArg(e2.getA(), varValues);
                    C0LitBool arg2 = (C0LitBool) convertArg(e2.getB(), varValues);
                    result = arg1.getVal() && arg2.getVal();
                    varValues.put(varName, new C0LitBool(result));
                }
            } else if (s1 instanceof C0If) {
                C0If s = (C0If) s1;
                C0Cmp cond = s.getCond();
                boolean flag = evalC0Comp(cond, varValues);
                
                
                //then if true you eval true branch, if false you eval else branch
                
                //going to throw all the statements in the right block following
                //the whole if exression
                if(flag) {
                    /*
                    the way this approach works: add all the new statements to the
                    current spot in the statement list, then backtrack
                    same thing in the else section.
                    This is not really better than recursion except that it might save some
                    stack space, i just felt like it would be more ugly if i changed
                    this function to return a pair of both the return value and the mapping,
                    so i could evaluate if and else blocks
                    */
                    
                    for(C0Stmt curr:((C0If) s1).getIfStmts()) {
                        iter.add(curr);
                    }
                    
                    for(C0Stmt curr:((C0If) s1).getIfStmts()) {
                        iter.previous();
                    }
                    //new recursive approach, call the interpreter on the current
                    //code block and get its value
//                    C0Program ifBlock = 
//                            new C0Program(varList, ((C0If) s1).getIfStmts(), , null);
//                    C0Literal result = C1Interpret(ifBlock);
                    //put it into the map
                    

                } else {
                    for(C0Stmt curr:((C0If) s1).getElseStmts()) {
                        iter.add(curr);
                    }
                    
                    for(C0Stmt curr:((C0If) s1).getElseStmts()) {
                        iter.previous();
                    }
                }
            }
        }
        //return the returnArg

        C0Arg arg = p.getReturnArg();

        if (arg instanceof C0Int || arg instanceof C0LitBool) {
            return ((C0Literal) arg);
        } else if (arg instanceof C0Var) {
            return varValues.get(((C0Var) arg).getName());
        }

        throw new Exception("did not get value");
    }

    public static C0Literal convertArg(C0Arg a, Map<String, C0Literal> varValues) {
        if (a instanceof C0Int) {
            return (C0Int) a;
        } else if (a instanceof C0LitBool) {
            return (C0LitBool) a;
        } else if (a instanceof C0Var) {
            return varValues.get(((C0Var) a).getName());
        }
        System.err.println("failure converting C0 arg");
        return null;

    }
    
    static boolean evalC0Comp (C0Cmp cond, Map<String, C0Literal> varValues) throws Exception {
        boolean flag = false;
        // structure: check if left and right are both bool,
                // or both int (you already did typecheck so you can assume that if first
                // arg is int, then they're both int)
                // if they're bool you can only check for equality
                // if they're both int then you switch on all operators

                //first check the type of both
                //compType is guaranteed to be initialized by the following
                //set of conditions but just to get rid of error it's set to null here
                //this may seem redundant but it's better to check before hand because you
                //don't know if when you get a variable, what type it is
                Class compType = null;
                if (cond.getA() instanceof C0Int) {
                    compType = C0Int.class;
                } else if (cond.getA() instanceof C0LitBool) {
                    compType = C0LitBool.class;
                } else if (cond.getA() instanceof C0Var) {
                    //compType = C0Var.class;
                    C0Literal varVal = varValues.get(((C0Var) cond.getA()).getName());
                    if(varVal == null) 
                        throw  new Exception("unable to find variable value");
                    if (varVal instanceof C0Int) {
                        compType = C0Int.class;
                    }
                    else if (varVal instanceof C0LitBool) {
                        compType = C0LitBool.class;
                    }
                }

                opValue op = cond.getOp();
                if (compType == C0LitBool.class) {
                    //this part should be checked in the type checker but it hasn't
                    //been implemented yet
                    if (op != opValue.EQ) {
                        System.err.println("bools can only be compared with EQ");
                    } else {
                        C0LitBool a= (C0LitBool ) convertArg(cond.getA(), varValues);
                        C0LitBool b= (C0LitBool ) convertArg(cond.getB(), varValues);
                        flag =(a.getVal() == b.getVal());
                        return flag;
                    }
                }
                else if(compType == C0Int.class) {
                    C0Int a= (C0Int ) convertArg(cond.getA(), varValues);
                    C0Int b= (C0Int ) convertArg(cond.getB(), varValues);
                    switch (op) {
                        case EQ:
                            flag =(a.getVal() == b.getVal());
                            return flag;
                        case GR:
                            flag =(a.getVal() > b.getVal());
                            return flag;
                        case GR_EQ:
                            flag =(a.getVal() >= b.getVal());
                            return flag;
                        case LESS:
                            flag =(a.getVal() < b.getVal());
                            return flag;
                        case LESS_EQ:
                            flag =(a.getVal() <= b.getVal());
                            return flag;
                        default:
                            break;
                    }
                        
                }
                throw new Exception("did not evaluate comparison");
    }

    static public void C1PrintProgram(C0Program p) {
        for (C0Stmt s1 : p.getStmtList()) {

            C0Assign s2 = (C0Assign) s1;
            C0Expression e = s2.getExp();
            String x = s2.getX().getName();

            //rhs is int
            if (e instanceof C0Int) {
                System.out.println(x + "=" + ((C0Int) e).getVal());
            }
            //rhs is var
            if (e instanceof C0Var) {
                String var2 = ((C0Var) e).getName();
                System.out.println(x + "=" + var2);
            }
            //rhs is read
            if (e instanceof C0Read) {
                System.out.println(x + "= read");
            }
            //rhs is neg
            if (e instanceof C0Neg) {
                C0Arg a = ((C0Neg) e).getA();
                if (a instanceof C0Int) {
                    System.out.println(x + "= -" + ((C0Int) a).getVal());
                } else if (a instanceof C0Var) {
                    String var2 = ((C0Var) a).getName();
                    System.out.println(x + "= -" + var2);

                }
            }
            //rhs is add
            if (e instanceof C0Add) {

                String first, second;
                C0Arg a = ((C0Add) e).getA();
                C0Arg b = ((C0Add) e).getB();
                //in both cases, if it's not an int, it's a var
                if (a instanceof C0Int) {
                    first = String.valueOf(((C0Int) a).getVal());
                } else {
                    first = ((C0Var) a).getName();
                }
                if (b instanceof C0Int) {
                    second = String.valueOf(((C0Int) b).getVal());
                } else {
                    second = ((C0Var) b).getName();
                }
                System.out.println(x + "=" + first + "+" + second);

            }
        }
    }

}
