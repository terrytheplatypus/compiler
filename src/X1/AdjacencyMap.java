/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package X1;

import java.util.HashMap;
import java.util.HashSet;
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
    
    public boolean addEdge(X1Arg a, X1Arg b) {
        if(adjMap.get(a)== null) adjMap.put(a, new HashSet<>());
        if(adjMap.get(b)== null) adjMap.put(b, new HashSet<>());
        adjMap.get(a).add(b);
        adjMap.get(b).add(a);
        return false;
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
    
}
