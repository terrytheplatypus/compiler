/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package X86_1_0;

import X86_1_1.X1Instr;
import X86_1_1.X1Var;
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
    
    
    
}
