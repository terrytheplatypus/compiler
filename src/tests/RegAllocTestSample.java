/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import R0.R0Program;
import X0.X0Program;
import X1.X1Program;
import X1.X1Var;
import static compilerPasses.PassMethods.*;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import static tests.StaticTestList.testList;
import static tests.testDirectComp.runPrint;

/**
 *
 * @author tennisers
 */
public class RegAllocTestSample {
    public static void main(String[] args) throws IOException {
        
        //first test contains method for Set <X1Arg>
        X1Var testV = new X1Var("teut");
        Set testS = new HashSet();
        testS.add(testV);
        X1Var testW = new X1Var("teut");
        System.out.println(testS.contains(testW));
        
        R0Program t = testList.get(5);
        //first test that it doesn't crash partway through
        X1Program p = select(flatten(uniquify(t)));
        X0Program x =  fix(assignModular(p, regAlloc(p)));
        System.out.print(printX0(x));
        runPrint(x);
    }
}
