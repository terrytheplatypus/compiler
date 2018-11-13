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
public class C2VecSet implements C0Expression{
    private C0Arg vec;
    private C0Int index;
    private C0Arg newVal;

    public C2VecSet(C0Arg vec, C0Int index, C0Arg newVal) {
        this.vec = vec;
        this.index = index;
        this.newVal = newVal;
    }

    public C0Int getIndex() {
        return index;
    }

    public C0Arg getNewVal() {
        return newVal;
    }

    public C0Arg getVec() {
        return vec;
    }
    
}
