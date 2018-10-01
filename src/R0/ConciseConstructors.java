/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package R0;

/**
 *
 * @author david
 */
public class ConciseConstructors {
    
    public static R0Int nInt(int a) {
        return new R0Int(a);
    }
    public static R0Var nVar(String a) {
        return new R0Var(a);
    }
    public static R0Add nAdd(R0Expression a, R0Expression b) {
        return new R0Add(a, b);
    }
    public static R0Neg nNeg(R0Expression a) {
        return new R0Neg(a);
    }
    public static R0Let nLet(R0Var x,R0Expression xe, R0Expression be) {
        return new R0Let(x,xe,be);
    }
    public static R0Read nRead(){
        return new R0Read();
    }
    
}
