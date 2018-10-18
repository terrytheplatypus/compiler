/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package C0;

import R0.R0Var;

/**
 *
 * @author david
 */
public class C0Var implements C0Arg{
    private String name;

    public C0Var(String name) {
        this.name = name;
    }
    
    public C0Var(R0Var a) {
        this.name = a.getName();
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public int hashCode() {
         return name.hashCode();
    } 
}
