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
public class X1GlobalValue implements X1Arg {

    private String name;

    public X1GlobalValue(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    
    
    @Override
    public String stringify() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
