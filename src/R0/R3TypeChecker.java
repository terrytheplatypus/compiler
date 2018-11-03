/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package R0;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author tennisers
 */
public class R3TypeChecker {
    
    //both the new R0Interpret and the ExpressionInterpret and the recursive
    //helper should return R0Literal (either boolean or int)
    public static R2TypedProgram R2TypeCheck(R0Program p) throws Exception {
        return new R2TypedProgram ( p.getExp(), R2ExpressionTypeCheck(p.getExp()));
    }
    
    
    /*
    
    */
    public static Class R2ExpressionTypeCheck(R0Expression e) throws Exception{
        Map <String, Class> varList = new HashMap<>();
        return R2ExpressionRecursiveTypeCheck(e,varList);
    }
    
    public static Class R2ExpressionRecursiveTypeCheck(R0Expression e, Map <String,Class> varList) throws Exception{
        //int case
        if(e instanceof R0Int) {
            //System.out.println("Int");
            return int.class;
        }
        //literal bool case
        if (e instanceof R0LitBool) {
            return boolean.class;
        }
        //var case
        else if(e instanceof R0Var){
            //System.out.println("Var");
            if(varList.get( ((R0Var) e).getName() )==null) {
                System.err.println("Error:variable used before declared");
            }
            return varList.get( ((R0Var) e).getName() );
        }
        //negate case
        else if(e instanceof R0Neg){
            //System.out.println("Neg");
            //if R0Neg is being performed, it assumes
            //e's value is int, if it isn't then it will throw error
            //thus, Java itself does type checking, limiting the amount
            // of type checking that I need to actually do
            return R2ExpressionRecursiveTypeCheck(((R0Neg) e).getChild(), varList);
            
        }
        //add case
        else if(e instanceof R0Add) {
            //System.out.println("Add");
            List <R0Expression> l = ((R0Add) e).getChildren();
            Class a = R2ExpressionRecursiveTypeCheck(l.get(0), varList);
            Class b = R2ExpressionRecursiveTypeCheck(l.get(1), varList);
            if(a == int.class && b == int.class ) return int.class;
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
            Class xe = R2ExpressionRecursiveTypeCheck(l.get(1), varList);
            varList.put(v.getName(), xe);
            //System.out.println("body expression");
            Class be = R2ExpressionRecursiveTypeCheck(l.get(2), varList);
            return be;
        }
        
        else if(e instanceof R0Read) {
            //for now read only takes int
            //also, read in the type checker would be weird because
            //potentially the user could enter the wrong type
            return int.class;
        }  
        //this is for the comparison
        else if (e instanceof R0Cmp) {
            //get literal value of lhs, literal value of rhs, compare
            Class lhs = R2ExpressionRecursiveTypeCheck(((R0Cmp) e).getA(),varList);
            Class rhs = R2ExpressionRecursiveTypeCheck(((R0Cmp) e).getB(),varList);
            R0CmpOp op = ((R0Cmp) e).getOp();
            
            if(lhs != rhs) throw new Exception("LHS and RHS of Cmp are mismatched");
            //this next part should throw exception if there's type mismatch
            
            return boolean.class;
            
        }  else if (e instanceof R0Not) {
            
            Class type = R2ExpressionRecursiveTypeCheck(((R0Not) e).getX(), varList);
            if(type != boolean.class) throw new Exception("Non-boolean argument to \"not\"");
            else return boolean.class;
            } else if(e instanceof R0If) {
                //condition should be bool, throw error otherwise
                //because checking values is not done in this type checker,
                //it can't actually give a correct return type for the if statement.
                //thus, even though it isn't the case, it will assume that the if and else
                //blocks have to have the same type, even though that isn't a requirement
                //of the language
                Class condType = R2ExpressionRecursiveTypeCheck(((R0If) e).getCond(), varList);
                if(condType != boolean.class) throw new  Exception("Condition of if stmt has unexpected type");
                
                    Class ifClass =  R2ExpressionRecursiveTypeCheck(((R0If) e).getRetIf(), varList);
                    
                
                    Class elseClass = R2ExpressionRecursiveTypeCheck(((R0If) e).getRetElse(), varList);
                    if(ifClass != elseClass)  throw new  Exception("If and else blocks do not have same type");
                    else return ifClass;
                
        
                
            } else if (e instanceof R0And) {
                Class a=  R2ExpressionRecursiveTypeCheck(((R0And) e).getA(), varList);
                
                Class b=  R2ExpressionRecursiveTypeCheck(((R0And) e).getB(), varList);
                
                if(a != b|| a != boolean.class) throw new Exception (" \"And\" expression had non-bool type args ");
                else return boolean.class;
            }
//        if(e instanceof R0)
        System.out.println("error type checking R0 expression");
        return null;
    }
    
    boolean R3TypeEquals(Object a, Object b) {
        if(a.getClass() == int.class && b.getClass() == int.class) return true;
        else if (a.getClass() == boolean.class )
    }
}
