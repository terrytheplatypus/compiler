/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package X0;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author david
 */
public class X0Reg implements X0Arg {
    //at first i'll assume that the user (me) is entering valid register names,
    //but then i may add a static array that checks if a register that is being initialized is
    //legal
    //static Set<String> regNames = new HashSet<>(Arrays.asList("a", "b"));
    private String name;

    public X0Reg(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    
}
