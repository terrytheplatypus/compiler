/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package C0;

import java.util.List;

/**
 *
 * @author tennisers
 */
public class C0If {
    private C0Cmp cond;
    private List <C0Stmt> ifStmts;
    private List <C0Stmt> elseStmts;

    public C0If(C0Cmp cond, List<C0Stmt> ifStmts, List<C0Stmt> elseStmts) {
        this.cond = cond;
        this.ifStmts = ifStmts;
        this.elseStmts = elseStmts;
    }

    public C0Cmp getCond() {
        return cond;
    }

    public List<C0Stmt> getElseStmts() {
        return elseStmts;
    }

    public List<C0Stmt> getIfStmts() {
        return ifStmts;
    }
    
}
