/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import R0.R0Program;
import  static R0.ConciseConstructors.*;
import static R0.R0Interpreter.R0Interpret;
import X86_1_1.X1Program;
import static compilerPasses.PassMethods.*;
import java.io.IOException;
import static X86_1_1.X1Interpreter.X1PrintWithLiveAfters;
import static tests.testDirectComp.runPrintV1;

/**
 *
 * @author tennisers
 */
public class TestLiveAfterCons {
    public static void main(String[] args) throws IOException {
        R0Program s = new R0Program(nLet(nVar("v"), nInt(1), 
                                    nLet(nVar("w"), nInt(46), 
                                    nLet(nVar("x"), nAdd(nVar("v"), nInt(7)),
                                    nLet(nVar("y"), nAdd(nInt(4), nVar("x")),
                                    nLet(nVar("z"), nAdd(nVar("x"), nVar("w")),
                                    nAdd(nVar("z"), nNeg(nVar("y")))))))));
        System.out.println(R0Interpret(s));
        X1Program x = uncoverLive(selectV1(flatten(uniquify(s))));
        X1PrintWithLiveAfters(x);
//        x.printLiveAfters();
        //int five = 2+ 2;
        //runPrint(compile1(s));
    }
}
