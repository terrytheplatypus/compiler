/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import R0.R0Program;
import R0.R2Interpreter;
import R0.R2TypeCheckedProgram;
import R0.R2TypeChecker;
import java.util.logging.Level;
import java.util.logging.Logger;
import static tests.TypecheckTestList.testList;

/**
 *
 * @author tennisers
 */
public class TypecheckTester {
    public static void main(String[] args) {
        
    int n = 0;
    for(R0Program r:testList) {
            System.out.println(n++);
        try {
            R2TypeCheckedProgram p =  R2TypeChecker.R2TypeCheck(r);
            System.out.println("value: "+p.getType().getName());
        } catch (Exception ex) {
//            Logger.getLogger(TypecheckTester.class.getName()).log(Level.SEVERE, null, ex);
//            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
            
        }
    }
}
