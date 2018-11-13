/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package C0;

import R0.R3Type;

/**
 *
 * @author tennisers
 */
public class C2Allocate implements C0Expression{
    private C0Int len;
    private R3Type type;

    public C2Allocate(C0Int len, R3Type type) {
        this.len = len;
        this.type = type;
    }

    public C0Int getLen() {
        return len;
    }

    public R3Type getType() {
        return type;
    }
    
    
}
