/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import static R0.ConciseConstructors.nAdd;
import static R0.ConciseConstructors.nInt;
import static R0.ConciseConstructors.nLet;
import static R0.ConciseConstructors.nVar;
import R0.R0Expression;
import static R0.R0Interpreter.ExpressionInterpret;
import static C0.C0Interpreter.C0Interpret;
import C0.C0Program;
import R0.R0Program;
import static compilerPasses.PassMethods.*;

import static C0.C0Interpreter.C0PrintProgram;
import static R0.R0Interpreter.R0Interpret;
import static X1.X1Interpreter.X1Interpret;
import X1.X1Program;
import static tests.StaticTestList.testList;

/**
 *
 * @author david
 */
public class TestSelect {
    public static void main(String[] args) {
        /*R0Expression nestedLetTest = 
                nLet(nVar("yo"),
                    nAdd(nInt(7),nInt(7))
                    ,nAdd(
                            nVar("yo"),
                            nLet(
                                nVar("yo"),
                                nInt(3),
                                nVar("yo")
                            )
                    )
                );
        R0Expression nestedLetUniquified = uniquify(nestedLetTest);
        R0Program p = new R0Program(nestedLetUniquified);
        System.out.println("value before flatten:"  + ExpressionInterpret(nestedLetUniquified));
        C0Program pp = flatten(p);
        C0PrintProgram(pp);
        System.out.println("value after flatten:" + C0Interpret(pp));*/
        for(R0Program r:testList) {
            R0Program p = uniquify(r);
            System.out.println("value after uniquify: "  + R0Interpret(p));
            C0Program pp = flatten(p);
            C0PrintProgram(pp);
            System.out.println("value after flatten: " + C0Interpret(pp));
            X1Program ppp = select(pp);
            System.out.println("value after select: " + X1Interpret(ppp));
            
        }
    }
}
