/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package R0;

import java.util.List;

/**
 *
 * @author tennisers
 */
public class R3Allocate implements R0Expression{

    private R0Int len;
    private R3Type type;
    
    /*
    as an abstraction allocate returns a vector
    */

    public R3Allocate(R0Int len, R3Type type) {
        this.len = len;
        this.type = type;
    }

    public R0Int getLen() {
        return len;
    }

    public R3Type getType() {
        return type;
    }
    
    
    
    @Override
    public List<R0Expression> getChildren() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
