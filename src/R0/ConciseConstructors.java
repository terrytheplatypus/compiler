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
    public static R0If nIf(R0Expression cond, R0Expression retIf, R0Expression retElse) {
        return new R0If(cond, retIf, retElse);
    }
    public static R0Cmp nCmp(R0CmpOp op, R0Expression a, R0Expression b) {
        return new R0Cmp(op, a, b);
    } public static R0LitBool nLitBool(boolean b) {
        return new R0LitBool(b);
    } public static R0Not nNot(R0Expression b) {
        return new R0Not(b);
    } public static R0And nAnd(R0Expression a, R0Expression b) {
        return new R0And(a, b);
    } public static R0Gr nGr () {
        return new R0Gr();
    } public static R0GrEq nGrEq () {
        return new R0GrEq();
    } public static R0Less nLess () {
        return new R0Less();
    } public static R0LessEq nLessEq () {
        return new R0LessEq();
    } public static R0Eq nEq () {
        return new R0Eq();
    } public static R0Program nProg(R0Expression e) {
        return new R0Program(e);
    }
    
}
