/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package X1;

import C0.C0Cmp.opValue;

/**
 *
 * @author tennisers
 */
public class X1set implements X1Instr {
    public static enum conditionCode{e,l,le,g,ge}
    X1Arg a;
    conditionCode cc;

    public X1set(conditionCode cc, X1Arg a) {
        this.cc = cc;
        this.a = a;
    }
    
    public static conditionCode R0CmpOpToCC (opValue op) {
        if(op == opValue.GR) {
            return conditionCode.g;
        } else if(op == opValue.GR_EQ) {
            return conditionCode.ge;
        } else if(op == opValue.EQ) {
            return conditionCode.e;
        } else if(op == opValue.LESS) {
            return conditionCode.l;
        } else if(op == opValue.LESS_EQ) {
            return conditionCode.le;
        }
        System.err.println("error converting R0CmpOp to string");
        return null;
    }
    
}
