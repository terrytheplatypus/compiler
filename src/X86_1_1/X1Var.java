/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package X86_1_1;

/**
 *
 * @author david
 */
public class X1Var implements X1Arg{
    private String name;

    public X1Var(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof X1Var) {
            X1Var a = (X1Var) obj;
            if(a.getName().equals(this.name)) {
                return true;
            }
            else return false;
        }
        //if not X1Var
        return false;
    }
    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
    @Override
    public String stringify() {
       return name;
    }
    
}
