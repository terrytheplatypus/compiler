/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package X0;

import X1.X1Arg;
import X1.X1Int;
import X1.X1Reg;

/**
 *
 * @author tennisers
 */
public class X1X0Conversion {
    //converts X1Int to X0Int and X1Reg to X0Reg
    public static X0Arg intRegConvert(X1Arg x) {
        if( x instanceof X1Int) return new X0Int(((X1Int) x).getVal());
        else if (x instanceof X1Reg) return new X0Reg(((X1Reg) x).getName());
        else{System.err.println("Error converting X1 Arg to X0 Arg"); return null;}
    }
}
