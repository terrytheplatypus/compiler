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
public class R3Collect implements R0Expression{

    R0Int bytes;

    public R3Collect(R0Int bytes) {
        this.bytes = bytes;
    }

    public R0Int getBytes() {
        return bytes;
    }
    
    
    
    @Override
    public List<R0Expression> getChildren() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
