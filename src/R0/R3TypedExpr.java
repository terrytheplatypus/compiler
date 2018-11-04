/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package R0;

import static R0.R3Type.R3Int;
import java.util.List;

/**
 *
 * @author tennisers
 */
public class R3TypedExpr implements R0Expression{
    private R0Expression e;
    private R3Type t;

    public R0Expression getE() {
        return e;
    }

    public R3Type getType() {
        return t;
    }

    public R3TypedExpr(R0Expression e, R3Type t) {
        this.e = e;
        this.t = t;
    }
    public R3TypedExpr(R0Expression e, Class c) {
        this.e = e;
        if(c == int.class) t = R3Int();
        if(c == boolean.class) t = R3Type.R3Bool();
    }
    
    public boolean typeEquals(R3TypedExpr b) {
        return this.t.equals(b.t);
    }

    @Override
    public List<R0Expression> getChildren() {
        return e.getChildren();
    }
}
