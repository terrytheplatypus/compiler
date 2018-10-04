/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package X1;

import java.util.List;

/**
 *
 * @author david
 */
public class X1Program {
    private List <X1Var> varList;
    private List<X1Instr> instrList;
    private X1Arg retArg;

    public X1Program(List<X1Var> varList, List<X1Instr> instrList, X1Arg retArg) {
        this.varList = varList;
        this.instrList = instrList;
        this.retArg = retArg;
    }

    public List<X1Instr> getInstrList() {
        return instrList;
    }

    public X1Arg getRetArg() {
        return retArg;
    }

    public List<X1Var> getVarList() {
        return varList;
    }
    
    
}
