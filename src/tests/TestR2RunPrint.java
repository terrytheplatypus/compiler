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
import X0.X0TypedProgram;
import X1.X1Interpreter;
import static X1.X1Interpreter.X1PrintWithLiveAfters;
import X1.X1Program;
import X1.X1TypedProgram;
import compilerPasses.PassMethods;
import static compilerPasses.PassMethods.buildInterference;
import static compilerPasses.PassMethods.compileControl;
import static compilerPasses.PassMethods.flatten;
import static compilerPasses.PassMethods.select;
import static compilerPasses.PassMethods.uncoverLive;
import static compilerPasses.PassMethods.uniquifyTypeCheckAndFlatten;
import java.io.IOException;
import java.util.Scanner;
import static tests.StaticTestList2.testList;
import static tests.testDirectComp.runPrintV2;

/**
 *
 * @author tennisers
 */
public class TestR2RunPrint {
        public static void main(String[] args) throws IOException, Exception {
        int n=-1;
        for(R0Test t:testList) {
            R0Program p = t.getProg();
            System.out.println("*********Test number " + (n++ +1)+"*********");
            
            //9 is the start of the conditional stuff
            //if(n < 8)continue;
            if(n < 9)continue;
            //if(n < 11)continue;
            System.out.println("Expected value: "+t.getExpectedVal());
            
            String expected = R2Interpreter.R2Interpret(p).stringify();
            
            System.out.println("R2 Interpreter value: "+expected);
//            C0Program flattened = flatten(p);
//            System.out.println("***Testing flatten***");
//            String flatten = C1Interpreter.C1Interpret(flattened).stringify();
//            System.out.println("Value after flatten: "+ flatten);
//            
            System.out.println("***Testing select***");
            
            C1TypedProgram p2 =  uniquifyTypeCheckAndFlatten(p);
            
            X1TypedProgram p3= select(p2);
            
            
            System.out.println("X1 interpreter: "+X1Interpreter.X1Interpret(p3));
            
            X0TypedProgram compiled = compileControl(p);
            
            System.out.println( PassMethods.printX0(compiled.getProg()) );
            
            runPrintV2(compiled.getProg());
            
            Scanner s = new Scanner(System.in);
            
            //X0Program compiled = compileRegAlloc(r);
            //System.out.println(printX0(compiled));
            //runPrintV2(compiled);
            
        }
    }
}
