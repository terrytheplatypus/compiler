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
public class R0TypedExpression {
    R0Expression e;
    Class type;

    public R0TypedExpression(R0Expression e, Class type) {
        this.e = e;
        this.type = type;
    }

    public R0Expression getE() {
        return e;
    }

    public Class getType() {
        return type;
    }
    
}
