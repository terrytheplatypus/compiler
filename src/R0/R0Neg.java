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
public class R0Neg implements R0Expression {

    R0Expression e;

    public R0Neg(R0Expression e) {
        this.e = e;
    }
    
    @Override
    public List<R0Expression> getChildren() {
        ArrayList<R0Expression> childs =
        new ArrayList<>(Arrays.asList(e));
        return childs;
    }
    
    public R0Expression getChild() {
        return e;
    }
    
}
