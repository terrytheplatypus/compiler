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
public class R2TypeCheckedProgram {
    
    R0Expression e;
    Class type;
    
    public R2TypeCheckedProgram(R0Expression e, Class t) {
        this.e = e;
        this.type = t;
    }

    public R0Expression getExp() {
        return e;
    }

    public Class getType() {
        return type;
    }
    
    
  
}
