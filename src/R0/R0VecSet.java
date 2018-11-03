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
public class R0VecSet implements R0Expression{
    
    private R0Expression vec;
    private R0Int index;
    private R0Expression newVal;

    public R0VecSet(R0Expression vec, R0Int index, R0Expression newVal) {
        this.vec = vec;
        this.index = index;
        this.newVal = newVal;
    }
    
    @Override
    public List<R0Expression> getChildren() {
       return new ArrayList<>(Arrays.asList(vec, index, newVal));
    }

    public R0Int getIndex() {
        return index;
    }

    public R0Expression getNewVal() {
        return newVal;
    }

    public R0Expression getVec() {
        return vec;
    }
    
    
}
