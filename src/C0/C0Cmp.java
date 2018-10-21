/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package C0;


import R0.R0CmpOp;
import R0.R0Eq;
import R0.R0Gr;
import R0.R0GrEq;
import R0.R0Less;
import R0.R0LessEq;

/**
 *
 * @author tennisers
 */
public class C0Cmp implements C0Expression{
    //unlike R0, i won't make a bunch of separate empty classes for each
    //operator because it's annoying and there would technically be more
    //overhead.
    
    //these enums
    public static enum opValue {EQ, LESS, LESS_EQ, GR, GR_EQ};
    
    public static opValue R0CmpOpToString (R0CmpOp op) {
        if(op instanceof R0Gr) {
            return opValue.GR;
        } else if(op instanceof R0GrEq) {
            return opValue.GR_EQ;
        } else if(op instanceof R0Eq) {
            return opValue.EQ;
        } else if(op instanceof R0Less) {
            return opValue.LESS;
        } else if(op instanceof R0LessEq) {
            return opValue.LESS_EQ;
        }
        System.err.println("error converting R0CmpOp to string");
        return null;
    }
    
    private opValue op;
    private C0Arg a;
    private C0Arg b;

    public C0Cmp(opValue op, C0Arg a, C0Arg b) {
        this.op = op;
        this.a = a;
        this.b = b;
    }

    public C0Arg getA() {
        return a;
    }

    public C0Arg getB() {
        return b;
    }

    public opValue getOp() {
        return op;
    }
    
    
    
    
    
}
