/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package C0;

/**
 *
 * @author tennisers
 */
public class C1TypedProgram {
    private C0Program p;
    //type can either be boolean.class or int.class
    private Class type;

    public C1TypedProgram(C0Program p, Class type) {
        this.p = p;
        this.type = type;
        
    }

    public C0Program getP() {
        return p;
    }

    public Class getType() {
        return type;
    }
    
}
