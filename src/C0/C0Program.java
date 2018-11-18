/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package C0;

import R0.R3Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author david
 */
public class C0Program {
    private List<C0Var> varList;
    private List <C0Stmt> stmtList;
    private C0Arg returnArg;
    
    //next elmt is added for garbage collection
    Map <String, R3Type> varsWithTypes;

    public C0Program(List<C0Var> varList, List<C0Stmt> stmtList, C0Arg returnArg, Map<String, R3Type> varsWithTypes) {
        this.varList = varList;
        this.stmtList = stmtList;
        this.returnArg = returnArg;
        this.varsWithTypes = varsWithTypes;
    }
    
    

    public C0Program(List<C0Var> varList, List<C0Stmt> stmtList, C0Arg returnArg) {
        this.varList = varList;
        this.stmtList = stmtList;
        this.returnArg = returnArg;
    }

    public C0Arg getReturnArg() {
        return returnArg;
    }

    public List<C0Stmt> getStmtList() {
        return stmtList;
    }
    /**
     * This is if you want a copy of the list
     * @return 
     */
    public List<C0Stmt> getStmtListCopy() {
        return new ArrayList(stmtList);
    }

    public List<C0Var> getVarList() {
        return varList;
    }

    public Map<String, R3Type> getVarsWithTypes() {
        return varsWithTypes;
    }
    
    
    
}
