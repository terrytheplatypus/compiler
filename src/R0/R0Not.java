/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package R0;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author tennisers
 */
public class R0Not implements R0Bool{
    R0Bool x;

    public R0Not(R0Bool x) {
        this.x = x;
    }

    
    
    public R0Bool getX() {
        return x;
    }

    @Override
    public List<R0Expression> getChildren() {
        return new ArrayList<>(Arrays.asList(x));
    }
    
}