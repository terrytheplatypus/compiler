/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package R0;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author david
 */
public class R3Interpreter {
    
    //for the intepreter to work roperly with references,
    //there has to be a list of the actual vectors in use by the program,
    //AND a map from vars to vectors
    //so the actual instances of each vector are in the list.
    //this means there does NOT have to be a separate map for variables with
    //vector values
    
    //static final Map <R0Var, R0Vector> vectorVals = new HashMap <>();
    
    static final List <R0Vector> vectors = new ArrayList <>();
    
    //both the new R0Interpret and the ExpressionInterpret and the recursive
    //helper should return R0Literal (either boolean or int)
    public static R0Basic R3Interpret(R0Program p) throws Exception {
        return ExpressionInterpret(p.getExp());
    }
    
    
    public static R0Basic ExpressionInterpret(R0Expression e) throws Exception{
        Map <String, R0Basic> varList = new HashMap<>();
        return ExpressionInterpretRecursive(e,varList);
    }
    public static R0Basic ExpressionInterpretRecursive(R0Expression e, Map <String, R0Basic> varList) throws Exception{
        //int case
        if(e instanceof R0Int) {
            //System.out.println("Int");
            return (R0Int) e;
        }
        //literal bool case
        if (e instanceof R0LitBool) {
            return (R0LitBool) e;
        }
        if(e instanceof R0Vector) {
            List <R0Expression> newExps = new ArrayList<>();
            
            for(R0Expression exp:((R0Vector) e).getElmts()) {
                newExps.add(ExpressionInterpretRecursive(exp, varList));
            }
            synchronized(vectors) {
            vectors.add(new R0Vector(newExps));
            }
            int index = vectors.size()-1;
            //this gives you the actual instance of the vector
            return vectors.get(index) ;
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
            R0Int init = (R0Int) ExpressionInterpretRecursive(((R0Neg) e).getChild(), varList);
            
            return new R0Int(-init.getVal());
        }
        //add case
        else if(e instanceof R0Add) {
            //System.out.println("Add");
            List <R0Expression> l = ((R0Add) e).getChildren();
            int sum =( (R0Int) ExpressionInterpretRecursive(l.get(0), varList)).getVal() + ( (R0Int) ExpressionInterpretRecursive(l.get(1), varList)).getVal();
            return new R0Int(sum);
            
        }
        //let case
        else if(e instanceof R0Let) {
            //System.out.println("Let");
            List <R0Expression> l = ((R0Let) e).getChildren();
            R0Var v = (R0Var) l.get(0);
            //System.out.println("assign to x");
            R0Basic xe = ExpressionInterpretRecursive(l.get(1), varList);
            varList.put(v.getName(), xe);
            
            //if it's a vector put it in the vectorVals map
//            if(xe instanceof R0Vector) {
//                synchronized(vectors) {
//                    //vectorVals.put(v, (R0Vector) xe);
//                    vectors.add((R0Vector) xe);
//                    varList.put(v.getName(), xe);
//                }
//            }
            
            //System.out.println("body expression");
            R0Basic be = ExpressionInterpretRecursive(l.get(2), varList);
            return be;
        }
        
        else if(e instanceof R0Read) {
            //either assume that read still only takes ints,
            // or let it take bool as well
            System.out.println("Enter a value:");
            return new R0Int( (new Scanner(System.in)).nextInt() );
        }  
        //this is for the comparison
        else if (e instanceof R0Cmp) {
            //get literal value of lhs, literal value of rhs, compare
            R0Basic lhs = ExpressionInterpretRecursive(((R0Cmp) e).getA(),varList);
            R0Basic rhs = ExpressionInterpretRecursive(((R0Cmp) e).getB(),varList);
            R0CmpOp op = ((R0Cmp) e).getOp();
            
            if(!lhs.getClass().equals(rhs.getClass())) throw new Exception("oops");
            //this next part should throw exception if there's type mismatch
            if(op instanceof R0Eq)
                return new R0LitBool(lhs.equals(rhs));
            else if((lhs instanceof R0Int) && rhs instanceof R0Int ) {
                int lhsVal = ((R0Int) lhs).getVal();
                int rhsVal = ((R0Int) rhs).getVal();
                if(op instanceof R0Gr) {
                    return new R0LitBool(lhsVal > rhsVal);
                } else if(op instanceof R0GrEq) {
                    return new R0LitBool(lhsVal >= rhsVal);
                }  else if(op instanceof R0Less) {
                    return new R0LitBool(lhsVal < rhsVal);
                } else if(op instanceof R0LessEq) {
                    return new R0LitBool(lhsVal <= rhsVal);
                }
            }
                else {
                System.err.println("Comparison error");
            }
        }  else if (e instanceof R0Not) {
                boolean b = ( (R0LitBool) ExpressionInterpretRecursive(((R0Not) e).getX(), varList)).getVal();
                return new R0LitBool(!b);
            } else if(e instanceof R0If) {
                boolean flag = ((R0LitBool) ExpressionInterpretRecursive(((R0If) e).getCond(), varList)).getVal();
                if(flag) {
                    R0Basic ret = ExpressionInterpretRecursive(((R0If) e).getRetIf(), varList);
                    if(ret instanceof R0Int) {
                        return (R0Int) ret;
                    } else if(ret instanceof R0LitBool) {
                        return (R0LitBool) ret;
                    }
                } else {
                    R0Basic ret = ExpressionInterpretRecursive(((R0If) e).getRetElse(), varList);
                    if(ret instanceof R0Int) {
                        return (R0Int) ret;
                    } else if(ret instanceof R0LitBool) {
                        return (R0LitBool) ret;
                    }
                }
                
            } else if (e instanceof R0And) {
                R0LitBool a= (R0LitBool) ExpressionInterpretRecursive(((R0And) e).getA(), varList);
                if(a.getVal() == false) {
                    return new R0LitBool(false);
                }
                R0LitBool b= (R0LitBool) ExpressionInterpretRecursive(((R0And) e).getB(), varList);
                boolean result = a.getVal() && b.getVal();
                return new R0LitBool(result);
            } else if( e instanceof R0VecSet) {
                //first argument can either be a vector or a variable, but bcuz it doesn't
                //return anything it would only be useful for variables
                R0Var var = (R0Var) ((R0VecSet) e).getVec();
                synchronized(vectors) {
                    R0Vector vec = (R0Vector) varList.get(var.getName());
                    //this should work because it's returning a reference
                    vec.getElmts().set(((R0VecSet) e).getIndex().getVal(), ((R0VecSet) e).getNewVal());
                
                }
                return new R0Void();
            } else if (e instanceof R0VecRef) {
                //this returns int
                R0Var var = (R0Var) ((R0VecRef) e).getVec();
                R0Vector vec = (R0Vector) varList.get(var.getName());
                if(vec == null)System.err.println("sdfsd");
                R0Expression ret= vec.getElmts().get(((R0VecRef) e).getIndex().getVal());
               R0Basic val =  ExpressionInterpretRecursive(ret, varList);
               return val;
            }
//        if(e instanceof R0)
        System.out.println("error interpreting R0 expression");
        return null;
    }
    //need separate methods for going through references to get and set the actual
    //vectors, as opposed to 
}
