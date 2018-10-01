package tests;


import static R0.ConciseConstructors.*;
import static R0.R0Interpreter.ExpressionInterpret;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author david
 */
public class TestR0Interpreter {
    public static void main(String args[]) {
        System.out.println("basic addition test");
        System.out.println(ExpressionInterpret(nAdd
            (nInt(2),nInt(3))
                )
        );
        System.out.println("basic negate test");
        System.out.println(ExpressionInterpret(nNeg(nAdd
            (nInt(3),nInt(7))
                ))
        );
        System.out.println("basic let test");
        int n = ExpressionInterpret(nLet(nVar("x"),
                                nAdd(nInt(2),nInt(3)),
                                nAdd(nInt(6),nVar("x"))));
        System.out.println(n);
        
        System.out.println("Basic read test");
        n = ExpressionInterpret(nLet(nVar("x"),
                                nRead(),
                                nAdd(nInt(6),nVar("x"))));
        System.out.println(n);
    }
}
