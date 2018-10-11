/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import R0.R0Interpreter;
import R0.R0Program;
import static compilerPasses.PassMethods.compile1;
import static compilerPasses.PassMethods.compileRegAlloc;
import static compilerPasses.PassMethods.printX0;
import java.io.IOException;
import static tests.StaticTestList.testList;
import static tests.testDirectComp.runPrint;

/**
 *
 * @author tennisers
 */
public class runTestsRegAlloc {
    public static void main(String[] args) throws IOException {
        int n=0;
        for(R0Program r:testList) {
            System.out.println(n++);
            if(n == 4)continue;
            System.out.println("Expected value: "+R0Interpreter.R0Interpret(r));
            runPrint(compileRegAlloc(r));
            
        }
    }
}