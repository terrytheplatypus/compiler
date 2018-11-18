/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testsR3;

import R0.R0Test;
import R0.R3Interpreter;
import static R0.R3Interpreter.R3Print;
import R0.R3TypeChecker;
import R0.R3TypedProgram;
import static compilerPasses.PassMethodsR3.exposeAllocation;
import static compilerPasses.PassMethodsR3.flatten;
import static compilerPasses.PassMethodsR3.uniquify;
import static testsR3.StaticTestList3.testList;

/**
 *
 * @author tennisers
 */
public class FlattenTester {
    public static void main(String[] args) {
        
    int n = 0;
    for(R0Test t:testList) {
            System.out.println(n++);
        try {
            if(n < 13) continue;
            System.out.println("Expected value: "+t.getExpectedVal());
            String result = R3Interpreter.R3Interpret(t.getProg()).stringify();
            
            System.out.println("Value from interpreter: "+ result );
            
            R3TypedProgram p =  R3TypeChecker.R3TypeCheck(t.getProg());
            //System.out.println("value: "+p.getType().toString());
            
            
            String result2 = R3Interpreter.R3Interpret(p).stringify();
            
            
            
            System.out.println("Value after type check: "+ result2 );
            
            R3TypedProgram u = uniquify(p);
            
            String result3 = R3Interpreter.R3Interpret(u).stringify();
            System.out.println("Value after uniquify: "+ result3 );
            
            R3TypedProgram exposed = exposeAllocation(u);
            
            String exposedPrint = R3Print(exposed);
            
            System.out.println(exposedPrint);
            
            String result4 = R3Interpreter.R3Interpret(exposed).stringify();
            System.out.println("Value after expose allocation: "+ result4 );
            
            flatten(exposed);
            
            if(!result.equals(result3) ) {
                throw new Exception("Test failed:"+ result
                + " is not equal to " + result3);
            }
            
            if(!result.equals(result4) ) {
                throw new Exception("Test failed:"+ result
                + " is not equal to " + result4);
            }
            
            
        } catch (Exception ex) {
//            Logger.getLogger(TypecheckTester.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
            
        }
    }
}
