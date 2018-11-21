/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package R0;

import java.util.Arrays;
import java.util.List;

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
    public static R0Let nLet(R0Expression x,R0Expression xe, R0Expression be) {
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
    }
    //should've added this bit earlier so i wouldn't have to keep track of "and" case
    //in the compiler at all
    public static R0If nAnd(R0Expression a, R0Expression b) {
        return nIf(a,b,nLitBool(false));
    }
    
//    public static R0And nAnd(R0Expression a, R0Expression b) {
//        return new R0And(a, b);
//    }
    public static R0Gr nGr () {
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
    } public static R0Vector nVec(List <R0Expression> elmts) {
        return new R0Vector (elmts);
    } public static R0Vector nVec(R0Expression ... elmts) {
        return new R0Vector (elmts);
    } public static R0VecSet nVecSet(R0Expression vec, R0Int n, R0Expression newVal) {
        return new R0VecSet(vec, n, newVal);
    } public static R0VecRef nVecRef(R0Expression vec , R0Int n) {
        return new R0VecRef(vec, n);
    } public static R0Void nVoid() {return new R0Void();}
    public static R0Test nTest(R0Program p, String expected) {
        return new R0Test(p, expected);
    }
    
    //maybe make a "begin" structure and a switch statement
    
    //here is begin structure, is recursively generated
    public static R0Expression nBegin(R0Expression ... exps) {
        return nBegin(Arrays.asList(exps));
    }
    public static R0Expression nBegin(List <R0Expression> exps) {
        if(exps.size() == 1) 
            return exps.get(0);
        else {
            R0Expression curr = exps.get(0);
            exps = exps.subList(1, exps.size());
            
            return nLet(nVar("_"), curr, nBegin(exps));
        }
    }
    public static R0Expression nBeginR3(R3TypedExpr ... exps) {
        return nBegin(Arrays.asList(exps));
    }
    public static R0Expression nBeginR3(List <R3TypedExpr> exps) {
        if(exps.size() == 1) 
            return exps.get(0);
        else {
            R0Expression curr = exps.get(0);
            exps = exps.subList(1, exps.size());
            
            return nLet(nVar("_"), curr, nBeginR3(exps));
        }
    }
    
    /*
    this function should only be used when vars and exprs are the same length.
    could also implement this to work with plain R0Expression but it's only used with 
    expose allocation so there's no point
    */
    public static R3TypedExpr assignList(List <R3TypedExpr> vars, List <R3TypedExpr> exprs) throws Exception {
        if(vars.size() == 1) {
            if(exprs.size()!= 1) 
                throw new Exception("assignlist has mismatched size of var and expr lists");
            else {
                return new R3TypedExpr(nLet(vars.get(0), exprs.get(0), vars.get(0)), 
                                            exprs.get(0).getType());
            }
        }
        R3Type expType = exprs.get(exprs.size()-1).getType();
        R3TypedExpr x = vars.remove(0);
        R3TypedExpr xe = exprs.remove(0);
        return new R3TypedExpr(nLet(x, xe, assignList(vars, exprs)), expType);
    }
    
}
