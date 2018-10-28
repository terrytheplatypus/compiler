/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import C0.C0Program;
import C0.C1Interpreter;
import C0.C1TypedProgram;
import R0.R0Program;
import R0.R0Test;
import R0.R2Interpreter;
import X1.X1Program;
import static compilerPasses.PassMethods.flatten;
import static compilerPasses.PassMethods.select;
import static compilerPasses.PassMethods.uniquifyTypeCheckAndFlatten;
import java.io.IOException;
import static tests.StaticTestList2.testList;

/**
 *
 * @author tennisers
 */
public class TestR2Select {
        public static void main(String[] args) throws IOException, Exception {
        int n=-1;
        for(R0Test t:testList) {
            R0Program p = t.getProg();
            System.out.println("*********Test number " + (n++ +1)+"*********");
            
            //9 is the start of the conditional stuff
            //if(n < 8)continue;
            if(n < 11)continue;
            
            String expected = R2Interpreter.R2Interpret(p).stringify();
            System.out.println("Expected value: "+t.getExpectedVal());
            System.out.println("R2 Interpreter value: "+expected);
            C0Program flattened = flatten(p);
            System.out.println("***Testing flatten***");
            String flatten = C1Interpreter.C1Interpret(flattened).stringify();
            System.out.println("Value after flatten: "+ flatten);
            
            C1TypedProgram p2 =  uniquifyTypeCheckAndFlatten(p);
            
            X1Program p3= select(p2.getP());
            
            
            //X0Program compiled = compileRegAlloc(r);
            //System.out.println(printX0(compiled));
            //runPrintV2(compiled);
            
        }
    }
}
