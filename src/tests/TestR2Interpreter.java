/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import R0.R2Interpreter;
import R0.R0Program;
import X86_1_0.X0Program;
import static compilerPasses.PassMethods.compileRegAlloc;
import java.io.IOException;
import static tests.StaticTestList.testList;
import static tests.testDirectComp.runPrintV2;

/**
 *
 * @author tennisers
 */
public class TestR2Interpreter {
    public static void main(String[] args) throws IOException, Exception {
        int n=-1;
        for(R0Program r:testList) {
            System.out.println(n++);
            if(n < 8)continue;
            
            String expected = R2Interpreter.R2Interpret(r).stringify();
            System.out.println("Expected value: "+expected);
            //X0Program compiled = compileRegAlloc(r);
            //System.out.println(printX0(compiled));
            //runPrintV2(compiled);
            
        }
    }
}
