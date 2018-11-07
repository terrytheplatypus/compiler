/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package R0;

import static R0.ConciseConstructors.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author tennisers
 */
public class R0Vector implements R0Expression, R0Basic, R0Persistent {
    
    private List <R0Expression> elmts;

    //this format is much nicer for writing new programs because i don't
    //need to stupidly declare a list
    public R0Vector (R0Expression ...newElmts) {
        this.elmts = Arrays.asList(newElmts);
    }
    
    public R0Vector(List<R0Expression> elmts) {
        this.elmts = elmts;
    }

    public List<R0Expression> getElmts() {
        return elmts;
    }
    
    

    @Override
    public List<R0Expression> getChildren() {
        return elmts;
    }

    /**
     * sets the value of the vector at index n (for the interpreter)
     * @param n 
     */
    void setAtIndex(int n, R0Expression newVal) {
        elmts.set(n, newVal);
    }

    /**
     * This assumes the vector you have has only literal and vector
     * entries (this is probably just for the interpreter)
     * I could also implement it for more cases which would be easy
     * if i finally add a name function to the R0 stuff.
     * @return 
     */
    @Override
    public String stringify() {
        //has to recurse through the children
        return stringifyHelper(this);
    }
    private String stringifyHelper (R0Basic e) {
        //if it's instance of int or bool
        if(e instanceof R0Literal ) {
            return ((R0Literal) e).stringify();
        }
            
        
        //if it's vector
        
        R0Vector e2 = (R0Vector) e;
        String result = "";
        result = "<";
        for (Iterator<R0Expression> it = e2.getElmts().iterator(); it.hasNext();) {
            R0Expression x = it.next();
            //assumes all elements are int bool void or vector
            
            if(x instanceof R3TypedExpr) x = ((R3TypedExpr) x).getE();
            
            R0Basic x1 = (R0Basic) x;
                if(x1 instanceof R0Literal) 
                    result += ((R0Literal) x1).stringify();
                else if(x1 instanceof R0Vector)
                    result +=  stringifyHelper(x1);
            if(it.hasNext()) result +=", ";
        }
        result +=">";
        
        return result;
    }
    
    /*
    This part is just testing that the stringify works
    */
    public static void main(String[] args) {
        R0Vector v = new R0Vector(nInt(2), nLitBool(false),
                        nVec(nInt(866),nInt(43)), nInt(62));
        String  hujdh = v.stringify();
        System.out.println(hujdh);
    }
    
}
