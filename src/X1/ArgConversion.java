/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package X1;

import C0.*;

/**
 *
 * @author david
 */
public class ArgConversion {
    public static X1Arg C0ToX1Arg(C0Arg x) {
        if(x instanceof C0Int) {
            return new X1Int(((C0Int) x).getVal());
        } else if(x instanceof C0Var) {
            return new X1Var(((C0Var) x).getName());
        } else if(x instanceof  C0LitBool) {
            //if it's a literal bool return 1 if it's true and 0 if false
            return ((C0LitBool) x).getVal() == true? new X1Int(1):new X1Int(0);
        }
        else {System.out.println("Error converting C0 arg to x0 arg");} return null;
    }
}
