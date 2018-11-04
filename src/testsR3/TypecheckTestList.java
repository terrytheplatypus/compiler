/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testsR3;

import tests.*;
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
    static List <R0Test> testList = testInitializer();
    static List testInitializer() {
        ArrayList <R0Test> l = new ArrayList<>();
        
        //incorrect programs first
        
        //0: invalid pair of and arguments
        l.add(nTest(nProg(nLet(nVar("x"), 
                nInt(2),
                //nInt(6),
                nAnd(nVar("x"), nLitBool(false)))),"invalid and arguments (int), error"));
        
        //1: bool is not given for first argument of if
        l.add(nTest(nProg(nIf(nInt(2), nInt(3), nInt(4))),"first arg of if is not bool, error"));
        
        //2: attempts to "not" a integer value
        
        l.add(nTest(nProg(nLet(nVar("q"), nInt(70), nNot(nVar("q")))),"attempts to NOT an"
                + "integer value, error "));
        
        
        
        //correct programs second
        
        //3
        //int
        l.add(nTest(new R0Program(nNeg(nAdd
            (nInt(3),nInt(7))
                )),"int"));
        
        //4
        //int
        R0Program s = new R0Program(nLet(nVar("v"), nInt(1), 
                                    nLet(nVar("w"), nInt(46), 
                                    nLet(nVar("x"), nAdd(nVar("v"), nInt(7)),
                                    nLet(nVar("y"), nAdd(nInt(4), nVar("x")),
                                    nLet(nVar("z"), nAdd(nVar("x"), nVar("w")),
                                    nAdd(nVar("z"), nNeg(nVar("y")))))))));
        l.add(nTest(s,"int"));
        //5
        l.add(nTest(tests.Utils.powerOf2(10),"int"));
        
        
        //6
        //bool
        l.add(nTest(nProg(
                nIf(nAnd(
                        nCmp(nGr(), nRead(), nInt(70)), nNot(nCmp(nGrEq(), nRead(), nInt(59)))),
                            nLitBool(true),
                            nLitBool(false))),"bool"));
        
        
        //next, test nested if combined with let
        
        //VECTOR TESTS
        
        //10
        //bad ones first
        //this one has a vector where it tries to vecref outta bounds
        l.add(nTest(nProg(nBegin(nLet(nVar("x"), 
                nVec(nInt(20), nInt(13)), 
                nVecRef(nVar("x"), nInt(2))))), "tries to vecref outta bounds, error"));
        //11
        /*
        this is actually a working one, maybe will add more bad ones but adding tests is painful
        */
        //make a vector of length three, int, bool, vec, if you enter 0 it returns int.
        //next two tests are for getting the other types
        
        l.add(nTest(nProg(nLet(nVar("yo"),
                nVec(nInt(1), nLitBool(true), nVec(nInt(2),nInt(3))),
                nVecRef(nVar("yo"), nInt(0)))), 
                "create <int, bool,<int, int>>, get 0th element (int)"));
        
        //12
        
        l.add(nTest(nProg(nLet(nVar("yo"),
                nVec(nInt(1), nLitBool(true), nVec(nInt(2),nInt(3))),
                nVecRef(nVar("yo"), nInt(1)))), 
                "create <int, bool,<int, int>>, get 1th element (bool)"));
        
        //13
        l.add(nTest(nProg(nLet(nVar("yo"),
                nVec(nInt(1), nLitBool(true), nVec(nInt(2),nInt(3))),
                nVecRef(nVar("yo"), nInt(2)))), 
                "create <int, bool,<int, int>>, get 2th element (<int,int>)"));
        
        return l;
    }
}
