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
public class R0Add implements R0Expression{
    R0Expression a;
    R0Expression b;

    public R0Add(R0Expression a, R0Expression b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public List<R0Expression> getChildren() {
       ArrayList<R0Expression> childs =
    new ArrayList<>(Arrays.asList(a,b));
       return childs;
    }
    
}
