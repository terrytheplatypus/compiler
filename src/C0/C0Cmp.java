/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package C0;

/**
 *
 * @author tennisers
 */
public class C0Cmp {
    //unlike R0, i won't make a bunch of separate empty classes for each
    //operator because it's annoying and there would technically be more
    //overhead.
    
    //these enums
    public static enum opValue {EQ, LESS, LESS_EQ, GR, GR_EQ};
    
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
