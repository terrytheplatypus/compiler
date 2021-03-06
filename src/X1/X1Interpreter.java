/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package X1;

import java.util.HashMap;
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
        }
        return 0;
    }
    
    //holds rax register value
       static int rax;
    
    //this assumes that the 2nd spot in a movq and addq and the only spot in negq are variables.
    // this is not in the language definition, but the flatten step makes this an invariant
    //similar thing for addq
    public static int X1Interpret(X1Program p){
        
        //initially populate <var name, int> map with var names and integer min value
        Map <String, Integer> varValues = new HashMap<>();
        for(X1Var v:p.getVarList())
            varValues.put(v.getName(), Integer.MIN_VALUE);
        //iterate thru all statements and update map accordingly
        
        
        
        for(X1Instr i:p.getInstrList()) {
            if(i instanceof X1movq) {
                int n = argEvaluate(((X1movq) i).getA(),varValues);
                String varName = ((X1Var)((X1movq) i).getB()).getName();
                varValues.put(varName, n);
            } else if(i instanceof X1callq) {
                if ("_read".equals(((X1callq) i).getLabel())) {
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
        }
        
        return Integer.MIN_VALUE;
    }
}
