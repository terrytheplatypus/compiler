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
public class R0VecRef implements R0Expression{
    private R0Expression vec;
    private R0Int index;

    public R0VecRef(R0Expression vec, R0Int index) {
        this.vec = vec;
        this.index = index;
    }

    
    
    @Override
    public List<R0Expression> getChildren() {
       return new ArrayList<>(Arrays.asList(vec, index));
    }

    public R0Int getIndex() {
        return index;
    }

    public R0Expression getVec() {
        return vec;
    }
    
}
