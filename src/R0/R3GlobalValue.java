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
public class R3GlobalValue implements R0Expression{
    String name;

    public R3GlobalValue(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    

    @Override
    public List<R0Expression> getChildren() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
