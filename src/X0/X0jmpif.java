/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package X0;

import X1.X1set.conditionCode;

/**
 *
 * @author tennisers
 */
public class X0jmpif implements X0Instr{
    private conditionCode cc;
    private X0label l;

    public X0jmpif(conditionCode cc, X0label l) {
        this.cc = cc;
        this.l = l;
    }

    public conditionCode getCc() {
        return cc;
    }

    public X0label getL() {
        return l;
    }
    
    
}
