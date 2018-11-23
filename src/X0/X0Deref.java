/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package X0;

/**
 *
 * @author david
 */
public class X0Deref implements X0Arg{
    private String name;
    private int offset;

    public X0Deref(String name, int offset) {
        this.name = name;
        this.offset = offset;
    }

    public String getName() {
        return name;
    }

    public int getOffset() {
        return offset;
    }
    
}
