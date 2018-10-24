/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package X86_1_1;

/**
 *
 * @author david
 */
public class X1callq implements X1Instr{
    private String label;

    public X1callq(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
    
}
