/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testsR3;

import tests.*;
import R0.R0Program;
import R0.R2Interpreter;
import R0.R2TypedProgram;
import R0.R2TypeChecker;
import R0.R3TypeChecker;
import R0.R3TypedProgram;
import java.util.logging.Level;
import java.util.logging.Logger;
import static testsR3.TypecheckTestList.testList;

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
            R3TypedProgram p =  R3TypeChecker.R3TypeCheck(r);
            System.out.println("value: "+p.getType().toString());
        } catch (Exception ex) {
//            Logger.getLogger(TypecheckTester.class.getName()).log(Level.SEVERE, null, ex);
//            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
            
        }
    }
}
