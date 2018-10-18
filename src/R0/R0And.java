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
public class R0And  implements R0Bool{
    
    private R0Expression a;
    private R0Expression b;

    //if the constructors are bools, 
    //then there's no need for type checking
    public R0And(R0Expression a, R0Expression b) {
        this.a = a;
        this.b = b;
    }
    
    
    
    @Override
    public List<R0Expression> getChildren() {
       ArrayList<R0Expression> childs =
       new ArrayList<>(Arrays.asList(a,b));
       return childs;    
    }

    public R0Expression getA() {
        return a;
    }

    public R0Expression getB() {
        return b;
    }
    
    
}
