/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package X0;

/**
 *
 * @author tennisers
 */
public class X0cmpq  implements X0Instr{
    private X0Arg a;
    private X0Arg b;

    public X0cmpq(X0Arg a, X0Arg b) {
        this.a = a;
        this.b = b;
    }

    public X0Arg getA() {
        return a;
    }

    public X0Arg getB() {
        return b;
    }
}
