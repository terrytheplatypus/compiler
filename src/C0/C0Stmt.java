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
public class C0Stmt {
    private C0Var x;
    private C0Expression exp;

    public C0Stmt(C0Var x, C0Expression exp) {
        this.x = x;
        this.exp = exp;
    }

    public C0Expression getExp() {
        return exp;
    }

    public C0Var getX() {
        return x;
    }
    
}
