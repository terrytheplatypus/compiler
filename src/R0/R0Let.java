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
 * @author david
 */
public class R0Let implements R0Expression{

    R0Expression x;
    R0Expression xe;
    R0Expression be;

    public R0Let(R0Expression x, R0Expression xe, R0Expression be) {
        this.x = x;
        this.xe = xe;
        this.be = be;
    }
    
    
    
    @Override
    public List<R0Expression> getChildren() {
        ArrayList<R0Expression> childs =
    new ArrayList<>(Arrays.asList(x,xe,be));
       return childs;
    }
    
}
