/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package R0;

import java.util.Iterator;
import java.util.List;

/**
 *
 * @author tennisers
 */
public class R3Type {
    private boolean isInt;
    private boolean isBool;
    private boolean isVector;
    
    private List <R3Type> elmtTypes;

    public R3Type(R0Basic b) {
        if(b instanceof R0Int) {
            isInt = true;
        } else if(b instanceof R0LitBool) {
            isBool = true;
        }
    }
    
    public R3Type(Class c)
    {
        if(c == int.class ||c ==R0Int.class) isInt = true;
        else if(c == boolean.class|| c== R0LitBool.class == true) isBool = true;
    }    
    public static R3Type R3Int() {
        return new R3Type(new R0Int(0));
    }
    
    public static R3Type R3Bool() {
        return new R3Type(new R0LitBool(true));
    }
    
    public R3Type(List <R3Type> elmtTypes) {
        isVector = true;
        this.elmtTypes =elmtTypes;
    }
    public boolean isInt() {
        return isInt;
    }
    
    public boolean isBool() {
        return isBool;
    }
    
    public boolean isVec() {
        return isVector;
    }

    public List<R3Type> getElmtTypes() {
        return elmtTypes;
    }
    
    

    /*
    there's a warning given because i don't make a hashCode function
    but that doesn't seem necessary because i won't be using the type as
    a key
    */
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof R3Type)) return false;
        R3Type obj1 = (R3Type) obj;
        if(this.isBool && obj1.isBool) return true;
        if(this.isInt && obj1.isInt) return true;
        if(this.isVector && obj1.isVector) {
            return this.elmtTypes.equals(obj1.elmtTypes);
        }
        return false;
    }

    @Override
    public String toString() {
        if(isInt == true) return "R0Int";
        if(isBool == true) return "R0Bool";
        if(isVector == true) {
            String ret = "<";
            for (Iterator<R3Type> it = elmtTypes.iterator(); it.hasNext();) {
                R3Type c = it.next();
                ret += c.toString();
                if(it.hasNext()) {
                    ret +=",";
                }
            }
            ret +=">";
        }
        return "error";
    }
    
    
    
    
}
