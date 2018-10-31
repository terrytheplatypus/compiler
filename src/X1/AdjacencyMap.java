/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package X1;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author tennisers
 */
public class AdjacencyMap {
    //here it's arg, but really it would just be var or reg
    private Map< X1Arg ,Set <X1Arg>> adjMap;

    public AdjacencyMap() {
        adjMap = new HashMap<>();
    }
    
    public void addVertex(X1Arg a) {
        adjMap.put(a, new HashSet <X1Arg>());
    }
    
    public void addEdge(X1Arg a, X1Arg b) {
        if(adjMap.get(a)== null) adjMap.put(a, new HashSet<>());
        if(adjMap.get(b)== null) adjMap.put(b, new HashSet<>());
        adjMap.get(a).add(b);
        adjMap.get(b).add(a);
        //return false;
    }
    public boolean isEdge(X1Arg a, X1Arg b) {
        if(adjMap.get(a)==null) return false;
        if(adjMap.get(a).contains(b)) return true;
        return false;
    }
    public Set<X1Arg> getEdges(X1Arg a) {
        if(adjMap.get(a)== null) adjMap.put(a, new HashSet<>());
        return adjMap.get(a);
    }
    
    //this main is to test that the API funcs work the way I'd expect
    public static void main(String[] args) {
        AdjacencyMap x = new AdjacencyMap();
        x.addEdge(new X1Var("iu"), new X1Var("bug"));
        x.addEdge(new X1Var("iu"), new X1Var("bu"));
        int mouse = 17;
    }
    
    public Set <X1Arg> getVarsAndRegs() {
        
        Set <X1Arg> allArgs = new HashSet<>();
        //adjMap.keySet doesn't work correctly for me (returns only Vars)
        for(Map.Entry< X1Arg ,Set <X1Arg>> c:adjMap.entrySet()) {
            allArgs.add(c.getKey());
        }
        return allArgs;
        //return adjMap.keySet();
    }
    
    public void print () {
        for(Map.Entry<X1Arg ,Set <X1Arg>> curs:adjMap.entrySet()) {
            System.out.print(curs.getKey().stringify()+": ");
            for(X1Arg curses:curs.getValue()) {
                System.out.print(curses.stringify()+",");
            }
            System.out.print("\n");
        }
    }

    /**
     * this is if you want to use the map directly
     * @return 
     */
    public Map<X1Arg, Set<X1Arg>> getActualMap() {
        return adjMap;
    }
    
    
}
