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
public class C0And implements C0Expression {
    private C0Arg a;
    private C0Arg b;

    public C0And(C0Arg a, C0Arg b) {
        this.a = a;
        this.b = b;
    }

    public C0Arg getA() {
        return a;
    }

    public C0Arg getB() {
        return b;
    }
    
    
    
}
