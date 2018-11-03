/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testsR3;

import R0.R0Test;
import R0.R3Interpreter;
import java.io.IOException;

/**
 *
 * @author tennisers
 */
public class R3InterpreterTest {
        public static void main(String[] args) throws IOException, Exception {
        int n=-1;
        for(R0Test t:StaticTestList3.testList) {
            System.out.println(n++);
            if(n < 12)continue;
            
            System.out.println("Expected value: "+t.getExpectedVal());
            String result = R3Interpreter.R3Interpret(t.getProg()).stringify();
            
            System.out.println("Value from interpreter: "+ result );
            //X0Program compiled = compileRegAlloc(r);
            //System.out.println(printX0(compiled));
            //runPrintV2(compiled);
            
        }
    }
    
}
