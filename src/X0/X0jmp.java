/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package X0;

/**
 *
 * @author tennisers
 */
public class X0jmp implements X0Instr{
    private X0label l;

    public X0jmp(X0label l) {
        this.l = l;
    }

    public X0label getL() {
        return l;
    }
    
    
}
