/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package R0;

import static R0.ConciseConstructors.nIf;
import static R0.ConciseConstructors.nNeg;
import static R0.R3Type.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author tennisers
 */
public class R3TypeChecker {
    
    
    
    
    /*
    type check only needs to affix type to vectors
    */
    public static R3TypedProgram R3TypeCheck(R0Program p) throws Exception{
        Map <String, R3Type> varList = new HashMap<>();
        R3TypedExpr result = R3RecursiveTypeCheck(p.getExp(),varList);
        return new R3TypedProgram(result, result.getType());
    }
    
    public static R3TypedExpr R3RecursiveTypeCheck(R0Expression e, Map <String,R3Type> varList) throws Exception{
        //int case
        if(e instanceof R0Int) {
            //System.out.println("Int");
            return new R3TypedExpr(e, R3Int());
        }
        //literal bool case
        if (e instanceof R0LitBool) {
            return new R3TypedExpr(e, R3Bool());
        }
        //var case
        else if(e instanceof R0Var){
            //System.out.println("Var");
            if(varList.get( ((R0Var) e).getName() )==null) {
                System.err.println("Error:variable used before declared");
            }
            R3Type varType = varList.get( ((R0Var) e).getName() );
            return new R3TypedExpr(e, varType);
        }
        //negate case
        else if(e instanceof R0Neg){
            //System.out.println("Neg");
            //if R0Neg is being performed, it assumes
            //e's value is int, if it isn't then it will throw error
            //thus, Java itself does type checking, limiting the amount
            // of type checking that I need to actually do
            R3TypedExpr arg = R3RecursiveTypeCheck(((R0Neg) e).getChild(), varList);
            return new R3TypedExpr(nNeg(arg),R3Int());
            
        }
        //add case
        else if(e instanceof R0Add) {
            //System.out.println("Add");
            List <R0Expression> l = ((R0Add) e).getChildren();
            R3TypedExpr a = R3RecursiveTypeCheck(l.get(0), varList);
            R3TypedExpr b = R3RecursiveTypeCheck(l.get(1), varList);
            R0Add sum = ConciseConstructors.nAdd(a, b);
            if(a.getType().isInt()  && b.getType().isInt() ) 
                return new R3TypedExpr(sum, R3Int());
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
            R3TypedExpr xe = R3RecursiveTypeCheck(l.get(1), varList);
            varList.put(v.getName(), xe.getType());
            //System.out.println("body expression");
            R3TypedExpr be = R3RecursiveTypeCheck(l.get(2), varList);
            R3TypedExpr varExpr  = new R3TypedExpr(v, xe.getType());
            return new R3TypedExpr(new R0Let(v, xe, be), be.getType());
        }
        
        else if(e instanceof R0Read) {
            //for now read only takes int
            //also, read in the type checker would be weird because
            //potentially the user could enter the wrong type
            return new R3TypedExpr(e, R3Int());
        }  
        //this is for the comparison
        else if (e instanceof R0Cmp) {
            //get literal value of lhs, literal value of rhs, compare
            R3TypedExpr lhs = R3RecursiveTypeCheck(((R0Cmp) e).getA(),varList);
            R3TypedExpr rhs = R3RecursiveTypeCheck(((R0Cmp) e).getB(),varList);
            R0CmpOp op = ((R0Cmp) e).getOp();
            
            if(!lhs.typeEquals(rhs)) throw new Exception("LHS and RHS of Cmp are mismatched");
            //this next part should throw exception if there's type mismatch
            
            return new R3TypedExpr(new R0Cmp(op, lhs, rhs), R3Bool());
            
        }  else if (e instanceof R0Not) {
            
            R3TypedExpr x = R3RecursiveTypeCheck(((R0Not) e).getX(), varList);
            if(!x.getType().isBool()) throw new Exception("Non-boolean argument to \"not\"");
            else return new R3TypedExpr(new R0Not(x), R3Bool());
            
            } else if(e instanceof R0If) {
                //condition should be bool, throw error otherwise
                //because checking values is not done in this type checker,
                //it can't actually give a correct return type for the if statement.
                //thus, even though it isn't the case, it will assume that the if and else
                //blocks have to have the same type, even though that isn't a requirement
                //of the language
                R3TypedExpr cond = R3RecursiveTypeCheck(((R0If) e).getCond(), varList);
                if(!cond.getType().isBool() ) throw new  Exception("Condition of if stmt has unexpected type");
                
                    R3TypedExpr ifClass =  R3RecursiveTypeCheck(((R0If) e).getRetIf(), varList);
                    
                
                    R3TypedExpr elseClass = R3RecursiveTypeCheck(((R0If) e).getRetElse(), varList);
                    if(!ifClass.typeEquals(elseClass) )  throw new  Exception("If and else blocks do not have same type");
                    else{ 
                        return new R3TypedExpr(nIf(cond, ifClass, elseClass), ifClass.getType());
                    }
                
        
                
            } else if (e instanceof R0And) {
                R3TypedExpr a=  R3RecursiveTypeCheck(((R0And) e).getA(), varList);
                
                R3TypedExpr b=  R3RecursiveTypeCheck(((R0And) e).getB(), varList);
                
                if(!a.typeEquals(b)|| !a.getType().isBool()) 
                    throw new Exception (" \"And\" expression had non-bool type args ");
                else return new R3TypedExpr(new R0And(a, b), R3Bool());
            } else if(e instanceof R0Vector) {
                //this has to 
                //the next list is technically a list of R3TypedExprs but
                //R3TypedExpr inherits from R0Expression, for the purposes of being used in
                //vector
                List <R0Expression> newVec = new ArrayList<>();
                List <R3Type> elmtTypes = new ArrayList<>();
                for(R0Expression curr:((R0Vector) e).getElmts()) {
                    R3TypedExpr a = R3RecursiveTypeCheck(curr, varList);
                    elmtTypes.add(a.getType());
                    newVec.add(a);
                }
                return new R3TypedExpr(new R0Vector(newVec), new R3Type(elmtTypes));
            } else if(e instanceof R0VecSet) {
                //if the vector part has an unitialized (or non-vec) variable,
                //throw and error
                R3TypedExpr vec = R3RecursiveTypeCheck(((R0VecSet) e).getVec(), varList);
                int indexVal = ((R0VecSet) e).getIndex().getVal();
                R3TypedExpr newVal = R3RecursiveTypeCheck(((R0VecSet) e).getNewVal(), varList);
                if(!vec.getType().isVec()) 
                    throw new Exception("non-vector expression in "
                        + "1st arg of vecset");
                //if the int arg is outside the bounds throw an error
                if(indexVal >=vec.getType().getElmtTypes().size())
                    throw new Exception(" out-of-bounds index in vecset");
                
                //type of the whole thing is void
                
                R0VecSet newSet = new R0VecSet(vec, 
                        ((R0VecSet) e).getIndex(), newVal);
                
                return new R3TypedExpr(newSet, Void.class);
                
            }  else if(e instanceof R0VecRef) {
                R0VecRef ref = (R0VecRef) e;
                R3TypedExpr vec = R3RecursiveTypeCheck(ref.getVec(), varList);
                int indVal = ref.getIndex().getVal();
                //if vec is not a vector type or the index is out-of-bounds, throw error
                
                if(!vec.getType().isVec()) throw new Exception("non-vector expression in "
                        + "1st arg of vecref");
                
                if(indVal >=vec.getType().getElmtTypes().size()) 
                    throw new Exception(" out-of-bounds index in vecset");
                
                //if both of those are fine, the return type is the type of the nth elemnt
                
                R3Type retType = vec.getType().getElmtTypes().get(indVal);
                return new R3TypedExpr(new R0VecRef(vec, ref.getIndex()), retType);
            }
//        if(e instanceof R0)
        System.out.println("error type checking R0 expression");
        return null;
    }
    
}
