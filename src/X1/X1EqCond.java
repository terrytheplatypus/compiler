/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package X1;

/**
 * this class is used specifically for if statements,
 * as the way they currently are, they only check that something is true
 * because the flatten step extracts any other test
 * that's why it's separate from cmpq, because it's an abstraction
 * @author tennisers
 */


public class X1EqCond {
    private X1Arg a;

    public X1EqCond(X1Arg a) {
        this.a = a;
    }

    public X1Arg getA() {
        return a;
    }
    
    
}
