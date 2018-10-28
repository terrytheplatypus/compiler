/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package X1;

/**
 *
 * @author tennisers
 */
public class X1ByteReg implements X1Arg{
    private String name;
    public X1ByteReg() {
        name = "al";
    }
    public X1ByteReg(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
     @Override
    public boolean equals(Object obj) {
        if(obj instanceof X1ByteReg) {
            X1ByteReg a = (X1ByteReg) obj;
            if(a.getName().equals(this.name)) {
                return true;
            }
            else return false;
        }
        //if not X1Reg
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
