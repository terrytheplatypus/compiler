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
public class R0Cmp implements R0Bool{
    private R0CmpOp op;
    private R0Expression a, b;
    //in this case I'm seeing why type checking is important because
    //here the program should be able to take two expressions,
    //but they could either both be bool or int, not one and the other.

    //however, workaround for this is to check in the constructor if they're
    //both the same type, if not then you throw error or something
    //but in java this is inconvenient because all exceptions have to be caught
    public R0Cmp(R0CmpOp op, R0Expression a, R0Expression b) {
        this.op = op;
        this.a = a;
        this.b = b;
    }

    @Override
    public List<R0Expression> getChildren() {
        ArrayList<R0Expression> childs =
       new ArrayList<>(Arrays.asList(a,b));
       return childs;
    }

    public R0CmpOp getOp() {
        return op;
    }

    public R0Expression getA() {
        return a;
    }

    public R0Expression getB() {
        return b;
    }
    
    
    
    
}
