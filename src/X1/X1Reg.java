/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package X1;

/**
 *
 * @author david
 */

//rax is the only register used in select pass so there doesn't need to be more info here
public class X1Reg implements X1Arg{
    private String name;
    public X1Reg() {
        name = "rax";
    }
    public X1Reg(String name) {
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
    
}
