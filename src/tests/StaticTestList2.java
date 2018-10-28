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
public class StaticTestList2 {
    //realized that defining the same tests over and over again would be a waste
    //so this is a centralized place to put tests
    //it would be nicer if i could initialize testList with an anonymous function
    static List <R0Test> testList = testInitializer();
    static List testInitializer() {
        ArrayList <R0Test> l = new ArrayList<>();
        
        //0
        l.add(new R0Test( new R0Program(nAdd
            (nInt(2),nInt(3))
                
        ), "5" ) );
        //1
        l.add(new R0Test(new R0Program(nNeg(nAdd
            (nInt(3),nInt(7))
                )),"-10"));
        //2
        l.add(new R0Test(new R0Program(nLet(nVar("x"),
                                nAdd(nInt(2),nInt(3)),
                                nAdd(nInt(6),nVar("x")))),"11"));
        //3
        l.add(new R0Test(new R0Program(nLet(nVar("x"),
                                nRead(),
                                nAdd(nInt(6),nVar("x")))),"read+6"));
        //4
        l.add(new R0Test(new R0Program(nLet(nVar("yo"),
                    nAdd(nInt(7),nInt(7))
                    ,nAdd(
                            nVar("yo"),
                            nLet(
                                nVar("yo"),
                                nInt(3),
                                nVar("yo")
                            )
                    )
                )),"17"));
        //5: value is 42
        R0Program s = new R0Program(nLet(nVar("v"), nInt(1), 
                                    nLet(nVar("w"), nInt(46), 
                                    nLet(nVar("x"), nAdd(nVar("v"), nInt(7)),
                                    nLet(nVar("y"), nAdd(nInt(4), nVar("x")),
                                    nLet(nVar("z"), nAdd(nVar("x"), nVar("w")),
                                    nAdd(nVar("z"), nNeg(nVar("y")))))))));
        l.add(new R0Test(s,"42"));
        //6
        l.add(new R0Test(Utils.powerOf2(10), "1024"));
        //7
        l.add(new R0Test(new R0Program(nLet(nVar("puo"), nRead(),
                            nLet(nVar("zuo"), nRead(),
                            nAdd(nVar("puo"),nVar("zuo"))))),"read1+read2"));
        //START OF CONTROL FLOW TESTS
        //8, simple if test
        l.add(new R0Test( new R0Program(nIf(nCmp(nEq(), nInt(72), nAdd(nInt(15), nInt(55)))
        ,nInt(77),nInt(44))),"if 15+55 < 72, return 77, else 44"));
        
        //9
        //next test: read two ints, x1, x2, see if first one is greater than 70,
        //see if next one is not (greater than or equal to 59) (less than 59)
        //if both are true (and) return true, if not, return false.
        l.add(new R0Test(nProg(
                nIf(nAnd(
                        nCmp(nGr(), nRead(), nInt(70)), nNot(nCmp(nGrEq(), nRead(), nInt(59)))),
                            nLitBool(true),
                            nLitBool(false))),"if read is greater than 70 and less than 59,"
                                    + "return true, else false"));
        
        //next test: letting x equal a literal bool value (mess with it to see what happens if
        //you give bad type, if you're not implementing type checker
        l.add( new R0Test(nProg(nLet(nVar("x"), 
                nLitBool(true),
                //nInt(6),
                nAnd(nVar("x"), nLitBool(false)))),"false"));
        
        //next, test nested if combined with let
        
        /*  y =     
            let(x = read
                if(x <40)
                    if(x>20)
                        return 1
                    else return 2
                return 3
        so if 20<x<40, y is 1, if x <=20, y is 2, if x >= 40, y is 3.
        These aren't enough test cases to guarantee that it works for everything
        but it's a good variety.
        */
        l.add(new R0Test(new R0Program(nLet(nVar("y"),
                    nLet(nVar("x"), nRead(),
                            nIf(nCmp(nLess(),nVar("x"), nInt(40)),
                                    nIf(nCmp(nGr(),nVar("x"),nInt(20)),
                                            nInt(1),
                                            nInt(2)),
                                    nInt(3))),nVar("y"))),"return 1 if 20 < read <40, "
                                            + "return 2 if read <= 20, "
                                            + "return 3 if read >= 40"));
        return l;
    }
}
