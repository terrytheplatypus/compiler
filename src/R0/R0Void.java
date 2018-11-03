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
public class R0Void implements R0Expression, R0Literal, R0Basic{

    public R0Void() {
    }

    
    
    @Override
    public List<R0Expression> getChildren() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String stringify() {
        return "void";
    }
    
}
