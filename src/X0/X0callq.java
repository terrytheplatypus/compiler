/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package X0;

/**
 *
 * @author david
 */
public class X0callq implements X0Instr{
    private String label;

    public X0callq(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
