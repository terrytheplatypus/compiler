/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package X1;

import java.util.List;

/**
 *
 * @author tennisers
 * 
 */
public class X1If implements X1Instr{
    private X1Arg cond;
    private List <X1Instr> ifs;
    private List <X1Instr> elses;

    /**
     * cond is an arg because ifs only check if something is true after the
     * flatten pass
     * @param cond
     * @param ifs
     * @param elses 
     */
    public X1If(X1Arg cond, List<X1Instr> ifs, List<X1Instr> elses) {
        this.cond = cond;
        this.ifs = ifs;
        this.elses = elses;
    }

    public X1Arg getCond() {
        return cond;
    }

    public List<X1Instr> getElses() {
        return elses;
    }

    public List<X1Instr> getIfs() {
        return ifs;
    }
    
    
}
