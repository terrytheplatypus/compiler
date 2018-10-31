/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package X1;

import java.util.ArrayList;
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
    
    //for liveness analysis
    private List <List <X1Var>> ifLivAfs;
    private List <List <X1Var>> elseLivAfs;

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

    public List<List<X1Var>> getElseLivAfs() {
        return elseLivAfs;
    }

    public List<List<X1Var>> getIfLivAfs() {
        return ifLivAfs;
    }

    public void setElseLivAfs(List<List<X1Var>> elseLivAfs) {
        this.elseLivAfs = elseLivAfs;
    }

    public void setIfLivAfs(List<List<X1Var>> ifLivAfs) {
        this.ifLivAfs = ifLivAfs;
    }
    
    public X1Program generateIfProgram () {
                
                X1Program ifsProgram = new X1Program(new ArrayList<>(),
                        this.ifs,
                        new X1Int(-1));
                ifsProgram.setLiveAfters(ifLivAfs);
                return ifsProgram;
    }
    
    public X1Program generateElsesProgram () {
                
                X1Program elsesProgram = new X1Program(new ArrayList<>(),
                        this.elses,
                        new X1Int(-1));
                elsesProgram.setLiveAfters(elseLivAfs);
                return elsesProgram;
    }
    
}
