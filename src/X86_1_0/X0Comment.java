/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package X86_1_0;

/**
 * Not actually a instruction but is used for debug
 * @author tennisers
 */
public class X0Comment implements X0Instr {
    String text;

    public X0Comment(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
    
}
