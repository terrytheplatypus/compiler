/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package X1;

/**
 *
 * @author tennisers
 */
public class X1movzbq implements X1Instr{
    private X1Arg a;
    private X1Arg b;

    public X1movzbq(X1Arg a, X1Arg b) {
        this.a = a;
        this.b = b;
    }

    public X1Arg getA() {
        return a;
    }

    public X1Arg getB() {
        return b;
    }
    
}
