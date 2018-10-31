/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package X0;

import X1.X1Instr;
import X1.X1Var;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author david
 */
public class X0Program {
    private List<X0Instr> instrList;

    public X0Program(List<X0Instr> instrList) {
        this.instrList = instrList;
    }

    public List<X0Instr> getInstrList() {
        return instrList;
    }
    
    public List<X0Instr> getInstrListCopy() {
        return new ArrayList <X0Instr> (instrList);
    }
    
}
