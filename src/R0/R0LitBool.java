/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package R0;

import java.util.ArrayList;
import java.util.List;

/**
 *Literal boolean value. used in return.
 * @author tennisers
 */
public class R0LitBool implements R0Bool,  R0Literal{

//    public static enum boolVals {TRUE, FALSE};
    
    private boolean val;

    public R0LitBool(boolean val) {
        this.val = val;
    }
    
    @Override
    public String stringify() {
        return String.valueOf(val);
    }
    public boolean getVal() {
        return val;
    }
    @Override
    public List<R0Expression> getChildren() {
        return null;
    }
    
    @Override
    public boolean equals(Object a) {
        if(!(a instanceof R0LitBool)) return false;
        else {
            return val == ((R0LitBool) a).getVal();
        }
    }

    @Override
    public int hashCode() {
        return new Boolean(val).hashCode(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
