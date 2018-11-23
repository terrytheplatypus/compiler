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
public class X1Deref implements X1Arg{

    private String name;
    private int offset;

    public X1Deref(String name, int offset) {
        this.name = name;
        this.offset = offset;
    }
    
    
    @Override
    public String stringify() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String getName() {
        return name;
    }

    public int getOffset() {
        return offset;
    }
    
    
    
}
