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
public class C0Neg implements C0Expression{
    private C0Arg a;

    public C0Neg(C0Arg a) {
        this.a = a;
    }

    public C0Arg getA() {
        return a;
    }
    
    
}
