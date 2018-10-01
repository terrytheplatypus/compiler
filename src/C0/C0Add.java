/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package C0;

/**
 *
 * @author david
 */
public class C0Add implements C0Expression{
    private C0Arg a, b;

    public C0Add(C0Arg a, C0Arg b) {
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
