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
public class X0RegWithOffset implements X0Arg{
    private String name;
    private int offset;

    public X0RegWithOffset(String name, int offset) {
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
