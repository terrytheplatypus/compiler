/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package X86_1_1;

/**
 *
 * @author david
 */
public class X1negq implements X1Instr{
    private X1Arg x;

    public X1negq(X1Arg x) {
        this.x = x;
    }

    public X1Arg getX() {
        return x;
    }
}
