/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package R0;

import static R0.ConciseConstructors.nProg;
import static R0.R3Type.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import javafx.util.Pair;

/**
 *
 * @author tennisers
 */
public class R3TypeChecker {
    
    
    private static Pair <R0Expression, R3Type> nPair(R0Expression e, R3Type t) {
        return new Pair <>(e, t);
    }
    
    /*
    type check only needs to affix type to vectors, so that's what it does.
    that minimizes changes that i need to make to everything else.
    */
    public static R3TypedProgram R3TypeCheck(R0Program p) throws Exception{
        Map <String, R3Type> varList = new HashMap<>();
        Pair <R0Expression, R3Type>  result = R3RecursiveTypeCheck(p.getExp(),varList);
        return new R3TypedProgram(nProg(result.getKey()), result.getValue());
    }
    
    public static Pair <R0Expression, R3Type> R3RecursiveTypeCheck(R0Expression e, Map <String,R3Type> varList) throws Exception{
        //int case
        if(e instanceof R0Int) {
            //System.out.println("Int");
            return nPair(e, R3Int());
        }
        //literal bool case
        if (e instanceof R0LitBool) {
            return nPair(e, R3Bool());
        }
        //var case
        else if(e instanceof R0Var){
            //System.out.println("Var");
            if(varList.get( ((R0Var) e).getName() )==null) {
                System.err.println("Error:variable used before declared");
            }
            R3Type varType = varList.get( ((R0Var) e).getName() );
            return nPair(e, varType);
        }
        //negate case
        else if(e instanceof R0Neg){
            //System.out.println("Neg");
            //if R0Neg is being performed, it assumes
            //e's value is int, if it isn't then it will throw error
            //thus, Java itself does type checking, limiting the amount
            // of type checking that I need to actually do
            return R3RecursiveTypeCheck(((R0Neg) e).getChild(), varList);
            
        }
        //add case
        else if(e instanceof R0Add) {
            //System.out.println("Add");
            List <R0Expression> l = ((R0Add) e).getChildren();
            Pair <R0Expression, R3Type> a = R3RecursiveTypeCheck(l.get(0), varList);
            Pair <R0Expression, R3Type> b = R3RecursiveTypeCheck(l.get(1), varList);
            if(a.getValue().isInt()  && b.getValue().isInt() ) 
                return nPair(e, R3Int());
            else {
                throw new Exception("Invalid type");
            }
            
        }
        //let case
        else if(e instanceof R0Let) {
            //System.out.println("Let");
            List <R0Expression> l = ((R0Let) e).getChildren();
            R0Var v = (R0Var) l.get(0);
            //System.out.println("assign to x");
            Pair <R0Expression, R3Type> xe = R3RecursiveTypeCheck(l.get(1), varList);
            varList.put(v.getName(), xe.getValue());
            //System.out.println("body expression");
            Pair <R0Expression, R3Type> be = R3RecursiveTypeCheck(l.get(2), varList);
            return be;
        }
        
        else if(e instanceof R0Read) {
            //for now read only takes int
            //also, read in the type checker would be weird because
            //potentially the user could enter the wrong type
            return nPair(e, R3Int());
        }  
        //this is for the comparison
        else if (e instanceof R0Cmp) {
            //get literal value of lhs, literal value of rhs, compare
            Pair <R0Expression, R3Type> lhs = R3RecursiveTypeCheck(((R0Cmp) e).getA(),varList);
            Pair <R0Expression, R3Type> rhs = R3RecursiveTypeCheck(((R0Cmp) e).getB(),varList);
            R0CmpOp op = ((R0Cmp) e).getOp();
            
            if(!lhs.getValue().equals(rhs.getValue())) throw new Exception("LHS and RHS of Cmp are mismatched");
            //this next part should throw exception if there's type mismatch
            
            return nPair(e, R3Bool());
            
        }  else if (e instanceof R0Not) {
            
            Pair <R0Expression, R3Type> x = R3RecursiveTypeCheck(((R0Not) e).getX(), varList);
            if(!x.getValue().isBool()) throw new Exception("Non-boolean argument to \"not\"");
            else return nPair(e, R3Bool());
            } else if(e instanceof R0If) {
                //condition should be bool, throw error otherwise
                //because checking values is not done in this type checker,
                //it can't actually give a correct return type for the if statement.
                //thus, even though it isn't the case, it will assume that the if and else
                //blocks have to have the same type, even though that isn't a requirement
                //of the language
                Pair <R0Expression, R3Type> condType = R3RecursiveTypeCheck(((R0If) e).getCond(), varList);
                if(!condType.getValue().isBool() ) throw new  Exception("Condition of if stmt has unexpected type");
                
                    Pair <R0Expression, R3Type> ifClass =  R3RecursiveTypeCheck(((R0If) e).getRetIf(), varList);
                    
                
                    Pair <R0Expression, R3Type> elseClass = R3RecursiveTypeCheck(((R0If) e).getRetElse(), varList);
                    if(!ifClass.getValue().equals(elseClass.getValue()) )
                        throw new  Exception("If and else blocks do not have same type");
                    else return ifClass;
                
        
                
            } else if (e instanceof R0And) {
                Pair <R0Expression, R3Type> a=  R3RecursiveTypeCheck(((R0And) e).getA(), varList);
                
                Pair <R0Expression, R3Type> b=  R3RecursiveTypeCheck(((R0And) e).getB(), varList);
                
                if(!a.getValue().equals(b.getValue())|| !a.getValue().isBool()) 
                    throw new Exception (" \"And\" expression had non-bool type args ");
                else return nPair(e, R3Bool());
            } else if(e instanceof R0Vector) {
                //this has to 
                //the next list is technically a list of Pair <R0Expression, R3Type>s but
                //Pair <R0Expression, R3Type> inherits from R0Expression, for the purposes of being used in
                //vector
                List <R0Expression> newVec = new ArrayList<>();
                List <R3Type> elmtTypes = new ArrayList<>();
                for(R0Expression curr:((R0Vector) e).getElmts()) {
                    Pair <R0Expression, R3Type> a = R3RecursiveTypeCheck(curr, varList);
                    elmtTypes.add(a.getValue());
                    //next part constructs a R3TypedExpr for each entry in the vector
                    newVec.add(new R3TypedExpr(e, a.getValue()));
                }
                return nPair(new R0Vector(newVec), new R3Type(elmtTypes));
            } else if(e instanceof R0VecSet) {
                //if the vector part has an unitialized (or non-vec) variable,
                //throw and error
                Pair <R0Expression, R3Type> vec = R3RecursiveTypeCheck(((R0VecSet) e).getVec(), varList);
                int indexVal = ((R0VecSet) e).getIndex().getVal();
                Pair <R0Expression, R3Type> newVal = R3RecursiveTypeCheck(((R0VecSet) e).getNewVal(), varList);
                if(!vec.getValue().isVec()) throw new Exception("non-vector expression in "
                        + "1st arg of vecset");
                //if the int arg is outside the bounds throw an error
                if(indexVal >=vec.getValue().getElmtTypes().size())
                    throw new Exception(" out-of-bounds index in vecset");
                
                //type of the whole thing is void
                
                R0VecSet newSet = new R0VecSet(vec.getKey(), 
                        ((R0VecSet) e).getIndex(), newVal.getKey());
                
                return nPair(newSet, R3Void());
                
            }  else if(e instanceof R0VecRef) {
                R0VecRef ref = (R0VecRef) e;
                Pair <R0Expression, R3Type> vec = R3RecursiveTypeCheck(ref.getVec(), varList);
                int indVal = ref.getIndex().getVal();
                //if vec is not a vector type or the index is out-of-bounds, throw error
                
                if(!vec.getValue().isVec()) throw new Exception("non-vector expression in "
                        + "1st arg of vecref");
                
                if(indVal >=vec.getValue().getElmtTypes().size()) 
                    throw new Exception(" out-of-bounds index in vecset");
                
                //if both of those are fine, the return type is the type of the nth elemnt
                
                R3Type retType = vec.getValue().getElmtTypes().get(indVal);
                return nPair(new R0VecRef(vec.getKey(), ref.getIndex()), retType);
            }
//        if(e instanceof R0)
        System.out.println("error type checking R0 expression");
        return null;
    }
    
}
