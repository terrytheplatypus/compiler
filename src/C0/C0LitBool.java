/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package C0;

import R0.R0Expression;
import R0.R0LitBool;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tennisers
 */
public class C0LitBool implements C0Literal{
    
    private boolean val;

    public C0LitBool(boolean val) {
        this.val = val;
    }
    
    public boolean getVal() {
        return val;
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
