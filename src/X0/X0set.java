/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package X0;

import C0.C0Cmp;
import X1.X1Arg;
import X1.X1set;

/**
 *
 * @author tennisers
 */
public class X0set implements X0Instr{
    //no reason to define a new conditionCode set for X0
    private final X0Arg a;
    private final X1set.conditionCode cc;

    public X0set(X1set.conditionCode cc, X0Arg a) {
        this.cc = cc;
        this.a = a;
    }

    public X0Arg getA() {
        return a;
    }

    public X1set.conditionCode getCc() {
        return cc;
    }
}
