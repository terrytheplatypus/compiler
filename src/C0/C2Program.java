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
 * @author tennisers
 */
public class C2Program {
    private List<C0Var> varList;
    Map <String, R3Type> varsWithTypes;
    private List <C0Stmt> stmtList;
    private C0Arg returnArg;
    
    //next elmt is added for garbage collection
    R3Type progType;

    public C2Program(List<C0Var> varList, Map<String, R3Type> varsWithTypes, List<C0Stmt> stmtList, R3Type progType, C0Arg returnArg) {
        this.varList = varList;
        this.varsWithTypes = varsWithTypes;
        this.stmtList = stmtList;
        this.returnArg = returnArg;
        this.progType = progType;
    }
    
    public C2Program (Map<String, R3Type> varsWithTypes, List<C0Stmt> stmtList, R3Type progType, C0Arg returnArg) {
        this.varsWithTypes = varsWithTypes;
        this.stmtList = stmtList;
        this.returnArg = returnArg;
        this.progType = progType;
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

    public R3Type getProgType() {
        return progType;
    }
    
    
    
}
