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
public class C2VecRef implements C0Expression{
    private C0Arg vec;
    private C0Int index;

    public C2VecRef(C0Arg vec, C0Int index) {
        this.vec = vec;
        this.index = index;
    }

    public C0Int getIndex() {
        return index;
    }

    public C0Arg getVec() {
        return vec;
    }
    
}
