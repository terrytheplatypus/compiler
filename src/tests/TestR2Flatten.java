/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import C0.C0Program;
import C0.C1Interpreter;
import R0.R0Program;
import R0.R2Interpreter;
import static compilerPasses.PassMethods.flatten;
import java.io.IOException;
import static tests.StaticTestList.testList;

/**
 *
 * @author tennisers
 */
public class TestR2Flatten {
        public static void main(String[] args) throws IOException, Exception {
        int n=-1;
        for(R0Program r:testList) {
            System.out.println("*********Test number " + (n++ +1)+"*********");
            
            //9 is the start of the conditional stuff
            //if(n < 8)continue;
            if(n < 11)continue;
            
            String expected = R2Interpreter.R2Interpret(r).stringify();
            System.out.println("Expected value: "+expected);
            C0Program flattened = flatten(r);
            System.out.println("***Testing flatten***");
            String flatten = C1Interpreter.C1Interpret(flattened).stringify();
            System.out.println("Value after flatten: "+ flatten);
            //X0Program compiled = compileRegAlloc(r);
            //System.out.println(printX0(compiled));
            //runPrintV2(compiled);
            
        }
    }
}
