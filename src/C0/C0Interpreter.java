/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package C0;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author david
 */
public class C0Interpreter {
    //this is to check that the flatten step gives the same answer
    //as the original expression
    
    //to evaluate it, you make a map from var names to ints and update it whenever stuff
    //is assigned, similarly to R0Interprter
    
    /*
    interpreter starts out by taking a C0 program, when it recurses through stuff it needs
    a map from String to Integer to keep track of the values of each variable
    */
    
    
    public static int C0Interpret(C0Program p){
        
        //initially populate <var name, int> map with var names and integer min value
        Map <String, Integer> varValues = new HashMap<>();
        for(C0Var v:p.getVarList())
            varValues.put(v.getName(), Integer.MIN_VALUE);
        //iterate thru all statements and update map accordingly
        
        for(C0Stmt s: p.getStmtList()) {
            
            C0Expression e = s.getExp();
            String x = s.getX().getName();
            
            //rhs is int
            if(e instanceof C0Int) {
                varValues.put(x, ((C0Int) e).getVal());
            }
            //rhs is var
            if(e instanceof C0Var) {
                String var2 = ((C0Var) e).getName();
                varValues.put(x, varValues.get(var2) );
            }
            //rhs is read
            if(e instanceof C0Read) {
                System.out.println("Enter a value:");
                int n = new Scanner(System.in).nextInt();
                varValues.put(x, n);
            }
            //rhs is neg
            if(e instanceof C0Neg) {
                C0Arg a = ((C0Neg) e).getA();
                if(a instanceof C0Int)
                    varValues.put(x, -((C0Int) a).getVal());
                else if(a instanceof C0Var) {
                    String var2 = ((C0Var) a).getName();
                    varValues.put(x, -varValues.get(var2) );
                }
            }
            //rhs is add
            if(e instanceof C0Add) {
                int first,second;
                C0Arg a = ((C0Add) e).getA();
                C0Arg b = ((C0Add) e).getB();
                //in both cases, if it's not an int, it's a var
                if(a instanceof C0Int)
                    first = ((C0Int) a).getVal();
                else  {
                    String var2 = ((C0Var) a).getName();
                    first = varValues.get(var2) ;
                }
                if(b instanceof C0Int)
                    second = ((C0Int) b).getVal();
                else {
                    String var2 = ((C0Var) b).getName();
                    second = varValues.get(var2) ;
                }
                varValues.put(x, first+second);
            }
        }
        //return the returnArg
        
        C0Arg arg = p.getReturnArg();
        
        if (arg instanceof C0Int) return ((C0Int) arg).getVal();
        else if(arg instanceof C0Var) return varValues.get(((C0Var) arg ).getName());
        
        return -9999;
    }
    static public void C0PrintProgram(C0Program p) {
        for(C0Stmt s: p.getStmtList()) {
            
            C0Expression e = s.getExp();
            String x = s.getX().getName();
            
            //rhs is int
            if(e instanceof C0Int) {
                System.out.println( x + "="+ ((C0Int) e).getVal());
            }
            //rhs is var
            if(e instanceof C0Var) {
                String var2 = ((C0Var) e).getName();
                System.out.println( x + "="+ var2);
            }
            //rhs is read
            if(e instanceof C0Read) {
                System.out.println( x + "= read");
            }
            //rhs is neg
            if(e instanceof C0Neg) {
                C0Arg a = ((C0Neg) e).getA();
                if(a instanceof C0Int) {
                    System.out.println( x + "= -"+ ((C0Int) a).getVal());
                }
                else if(a instanceof C0Var) {
                    String var2 = ((C0Var) a).getName();
                    System.out.println( x + "= -"+ var2);
                        
                }
            }
            //rhs is add
            if(e instanceof C0Add) {
                
                String first,second;
                C0Arg a = ((C0Add) e).getA();
                C0Arg b = ((C0Add) e).getB();
                //in both cases, if it's not an int, it's a var
                if(a instanceof C0Int)
                    first = String.valueOf(((C0Int) a).getVal());
                else  {
                    first = ((C0Var) a).getName();
                }
                if(b instanceof C0Int)
                    second = String.valueOf(((C0Int) b).getVal());
                else {
                    second = ((C0Var) b).getName();
                }
                System.out.println(x +"="+first+"+"+second);
                
            }
        }
    }
    
}
