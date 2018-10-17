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
public class R0If implements R0Expression{
    
    R0Bool cond;
    R0Expression retIf;
    R0Expression retElse;

    public R0If(R0Bool cond, R0Expression retIf, R0Expression retElse) {
        this.cond = cond;
        this.retIf = retIf;
        this.retElse = retElse;
    }
    
    

    @Override
    public List<R0Expression> getChildren() {
       ArrayList<R0Expression> childs =
       new ArrayList<>(Arrays.asList(cond, retIf,retElse));
       return childs;    
    }

    public R0Bool getCond() {
        return cond;
    }

    public R0Expression getRetElse() {
        return retElse;
    }

    public R0Expression getRetIf() {
        return retIf;
    }
    
}
