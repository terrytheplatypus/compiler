/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package X0;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tennisers
 * 
 * This is an abstraction that's not in the final x86
 */
public class X0If implements X0Instr{
    private X0Arg cond;
    private List <X0Instr> ifs;
    private List <X0Instr> elses;

    public X0If(X0Arg cond, List<X0Instr> ifs, List<X0Instr> elses) {
        this.cond = cond;
        this.ifs = ifs;
        this.elses = elses;
    }
    
    
    
    public X0Arg getCond() {
        return cond;
    }

    public List<X0Instr> getElses() {
        return elses;
    }

    public List<X0Instr> getIfs() {
        return ifs;
    }
    
    public X0Program generateIfProgram () {
                
                X0Program ifsProgram = new X0Program(this.ifs);
                return ifsProgram;
    }
    
    public X0Program generateElsesProgram () {
                
                X0Program elsesProgram = new X0Program(this.elses);
                return elsesProgram;
    }
    
    
}
