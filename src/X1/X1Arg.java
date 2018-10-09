/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package X1;

/**
 * this is used for the select pass
 * @author david
 */
public interface X1Arg {
    
    //these are here because of the graph coloring algorithm, which takes either
    //vars or regs for the coloring
    @Override
    public abstract boolean equals(Object obj);
    @Override
    public abstract int hashCode();
    
    public abstract String stringify();
}
