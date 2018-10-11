/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import R0.R0Interpreter;
import R0.R0Program;
import static compilerPasses.PassMethods.*;
import static tests.StaticTestList.testList;

/**
 *
 * @author david
 */
public class outputTests {
    public static void main(String[] args) {
        int n=0;
        for(R0Program r:testList) {
            System.out.println(n++);
            System.out.println("value: "+R0Interpreter.R0Interpret(r));
            System.out.println( printX0(compile1(r)) );
            
        }
    }
}
