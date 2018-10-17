/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package R0;

import java.util.List;

/**
 *
 * @author david
 */
public class R0Int implements R0Expression, R0Literal {
    private int val;
    public R0Int(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }
    
    @Override
    public List <R0Expression> getChildren(){return null;}

    @Override
    public String stringify() {
        return String.valueOf(val);
    }
    @Override
    public boolean equals(Object a) {
        if(!(a instanceof R0Int)) return false;
        else {
            return val == ((R0Int) a).getVal();
        }
    }

    @Override
    public int hashCode() {
        return new Integer(val).hashCode();
    }
}
