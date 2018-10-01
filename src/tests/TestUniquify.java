/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;
import R0.*;
import static R0.ConciseConstructors.*;
import static compilerPasses.PassMethods.*;
import static R0.R0Interpreter.ExpressionInterpret;
import static R0.R0Interpreter.R0Interpret;

/**
 *
 * @author david
 */
public class TestUniquify {
    public static void main(String[] args) {
        R0Expression basicLetTest;
        R0Expression nestedLetTest = 
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
        R0Program nestedLetUniquified = uniquify(new R0Program(nestedLetTest));
        System.out.println("Result before uniquify is " + ExpressionInterpret(nestedLetTest));
        System.out.println("Result after uniquify is " + R0Interpret(nestedLetUniquified));
    }
}
