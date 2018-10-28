/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import R0.R0Interpreter;
import R0.R0Program;
import X0.X0Program;
import static compilerPasses.PassMethods.compile1;
import static compilerPasses.PassMethods.compileRegAlloc;
import static compilerPasses.PassMethods.printX0;
import java.io.IOException;
import java.util.Scanner;
import static tests.StaticTestList.testList;
import static tests.testDirectComp.runPrintV1;
import static tests.testDirectComp.runPrintV2;

/**
 *
 * @author tennisers
 */
public class runTestsRegAlloc {
    public static void main(String[] args) throws IOException {
        int n=0;
        for(R0Program r:testList) {
            System.out.println(n++);
            //if(n != 8)continue;
            int expected = R0Interpreter.R0Interpret(r);
            System.out.println("Expected value: "+expected);
            X0Program compiled = compileRegAlloc(r);
            //System.out.println(printX0(compiled));
            runPrintV2(compiled);
            
            Scanner s = new Scanner(System.in);
            s.nextLine();
        }
    }
}