/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import R0.*;
import static compilerPasses.PassMethods.printX0;
import static compilerPasses.PassMethods.compile1;

/**
 *
 * @author david
 */
public class Utils {
    //generates an R0 program that should yield 2^n
    public static R0Program powerOf2 (int n) {
        return new R0Program(powExp(n));
    }
    public static R0Expression powExp(int n) {
        if(n == 1) {
            return new R0Add(new R0Int(1),new R0Int(1));
        } else if(n == 0) {
            return new R0Int(1);
        }
        return(new R0Add(powExp(n-1),powExp(n-1)));
    }
    public static void main(String[] args) {
        System.out.println("2^12 = " +R0Interpreter.R0Interpret(powerOf2(12)));
        System.out.println(printX0(compile1(powerOf2(12))));
    }
}
