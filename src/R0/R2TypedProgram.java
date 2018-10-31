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
public class R2TypedProgram {
    
    R0Expression e;
    Class type;
    
    public R2TypedProgram(R0Expression e, Class t) {
        this.e = e;
        this.type = t;
    }
    
    public R2TypedProgram(R0Program p, Class t) {
        this.e = p.getExp();
        this.type = t;
    }

    public R0Expression getExp() {
        return e;
    }

    public Class getType() {
        return type;
    }
    
    public R0Program getProg() {
        return new R0Program(e);
    }
    
  
}
