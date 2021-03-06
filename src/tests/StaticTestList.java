/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import static R0.ConciseConstructors.*;
import static R0.R0Interpreter.ExpressionInterpret;
import R0.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author david
 */
public class StaticTestList {
    //realized that defining the same tests over and over again would be a waste
    //so this is a centralized place to put tests
    //it would be nicer if i could initialize testList with an anonymous function
    static List <R0Program> testList = testInitializer();
    static List testInitializer() {
        ArrayList <R0Program> l = new ArrayList<>();
        l.add(new R0Program(nAdd
            (nInt(2),nInt(3))
                
        ));
        l.add(new R0Program(nNeg(nAdd
            (nInt(3),nInt(7))
                )));
        l.add(new R0Program(nLet(nVar("x"),
                                nAdd(nInt(2),nInt(3)),
                                nAdd(nInt(6),nVar("x")))));
        l.add(new R0Program(nLet(nVar("x"),
                                nRead(),
                                nAdd(nInt(6),nVar("x")))));
        l.add(new R0Program(nLet(nVar("yo"),
                    nAdd(nInt(7),nInt(7))
                    ,nAdd(
                            nVar("yo"),
                            nLet(
                                nVar("yo"),
                                nInt(3),
                                nVar("yo")
                            )
                    )
                )));
        return l;
    }
}
