/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package R0;

/**
 *
 * @author tennisers
 */
public class R3TypedProgram {
    private R0Program p;
    private R3Type t;

    public R3TypedProgram(R0Program p, R3Type t) {
        this.p = p;
        this.t = t;
    }

    public R0Program getProg() {
        return p;
    }

    public R3Type getType() {
        return t;
    }
    
}
