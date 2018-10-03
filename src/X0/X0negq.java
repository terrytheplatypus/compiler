/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package X0;

/**
 *
 * @author david
 */
public class X0negq implements X0Instr{
    private X0Arg x;

    public X0negq(X0Arg x) {
        this.x = x;
    }

    public X0Arg getX() {
        return x;
    }
}
