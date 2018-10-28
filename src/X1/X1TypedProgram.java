/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package X1;

/**
 *
 * @author tennisers
 */
public class X1TypedProgram {
   private X1Program prog;
   private Class type;

    public X1TypedProgram(X1Program p, Class type) {
        this.prog = p;
        this.type = type;
    }

    public X1Program getProg() {
        return prog;
    }

    public Class getType() {
        return type;
    }
   
    
}
