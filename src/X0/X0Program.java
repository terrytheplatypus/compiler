/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package X0;

import X1.X1Instr;
import X1.X1Var;
import java.util.List;

/**
 *
 * @author david
 */
public class X0Program {
    private List <X1Var> varList;
    private List<X1Instr> instrList;

    public X0Program(List<X1Var> varList, List<X1Instr> instrList) {
        this.varList = varList;
        this.instrList = instrList;
    }

    public List<X1Instr> getInstrList() {
        return instrList;
    }

    public List<X1Var> getVarList() {
        return varList;
    }
    
    
    
}
