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
    
    
}
