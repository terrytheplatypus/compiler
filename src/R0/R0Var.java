/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package R0;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author david
 */
public class R0Var implements R0Expression {
    private String name;
    public R0Var(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    
    @Override
    public List <R0Expression> getChildren(){return null;}
    
    @Override
    public int hashCode() {
         return name.hashCode();
    }    
    
}
