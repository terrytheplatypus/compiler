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
public class X0TypedProgram {
    private X0Program prog;
   private Class type;

    public X0TypedProgram(X0Program p, Class type) {
        this.prog = p;
        this.type = type;
    }

    public X0Program getProg() {
        return prog;
    }

    public Class getType() {
        return type;
    }
   
   
}
