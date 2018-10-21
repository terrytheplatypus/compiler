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
public class TypecheckTestList {
    //realized that defining the same tests over and over again would be a waste
    //so this is a centralized place to put tests
    //it would be nicer if i could initialize testList with an anonymous function
    static List <R0Program> testList = testInitializer();
    static List testInitializer() {
        ArrayList <R0Program> l = new ArrayList<>();
        
        //incorrect programs first
        
        //0: invalid pair of and arguments
        l.add(nProg(nLet(nVar("x"), 
                nInt(2),
                //nInt(6),
                nAnd(nVar("x"), nLitBool(false)))));
        
        //1: bool is not given for first argument of if
        l.add(nProg(nIf(nInt(2), nInt(3), nInt(4))));
        
        //2: attempts to "not" a integer value
        
        l.add(nProg(nLet(nVar("q"), nInt(70), nNot(nVar("q")))));
        
        
        
        //correct programs second
        
        
        //int
        l.add(new R0Program(nNeg(nAdd
            (nInt(3),nInt(7))
                )));
        
        //int
        R0Program s = new R0Program(nLet(nVar("v"), nInt(1), 
                                    nLet(nVar("w"), nInt(46), 
                                    nLet(nVar("x"), nAdd(nVar("v"), nInt(7)),
                                    nLet(nVar("y"), nAdd(nInt(4), nVar("x")),
                                    nLet(nVar("z"), nAdd(nVar("x"), nVar("w")),
                                    nAdd(nVar("z"), nNeg(nVar("y")))))))));
        l.add(s);
        //6
        l.add(Utils.powerOf2(10));
        
        
        //9
        //bool
        l.add(nProg(
                nIf(nAnd(
                        nCmp(nGr(), nRead(), nInt(70)), nNot(nCmp(nGrEq(), nRead(), nInt(59)))),
                            nLitBool(true),
                            nLitBool(false))));
        
        
        //next, test nested if combined with let
        return l;
    }
}
