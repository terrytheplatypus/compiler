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
public class C2Collect implements C0Stmt{
    C0Int bytes;

    public C2Collect(C0Int bytes) {
        this.bytes = bytes;
    }

    public C0Int getBytes() {
        return bytes;
    }
    
    
}
