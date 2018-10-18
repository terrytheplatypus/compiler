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
 * @author david
 */
public class R2Interpreter {
    
    //both the new R0Interpret and the ExpressionInterpret and the recursive
    //helper should return R0Literal (either boolean or int)
    public static R0Literal R0Interpret(R0Program p) throws Exception {
        return ExpressionInterpret(p.getExp());
    }
    
    
    public static R0Literal ExpressionInterpret(R0Expression e) throws Exception{
        Map <String, R0Literal> varList = new HashMap<>();
        return ExpressionInterpretRecursive(e,varList);
    }
    public static R0Literal ExpressionInterpretRecursive(R0Expression e, Map <String, R0Literal> varList) throws Exception{
        //int case
        if(e instanceof R0Int) {
            //System.out.println("Int");
            return (R0Int) e;
        }
        //literal bool case
        if (e instanceof R0LitBool) {
            return (R0LitBool) e;
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
            R0Literal xe = ExpressionInterpretRecursive(l.get(1), varList);
            varList.put(v.getName(), xe);
            //System.out.println("body expression");
            R0Literal be = ExpressionInterpretRecursive(l.get(2), varList);
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
            R0Literal lhs = ExpressionInterpretRecursive(((R0Cmp) e).getA(),varList);
            R0Literal rhs = ExpressionInterpretRecursive(((R0Cmp) e).getB(),varList);
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
                    R0Literal ret = ExpressionInterpretRecursive(((R0If) e).getRetIf(), varList);
                    if(ret instanceof R0Int) {
                        return (R0Int) ret;
                    } else if(ret instanceof R0LitBool) {
                        return (R0LitBool) ret;
                    }
                } else {
                    R0Literal ret = ExpressionInterpretRecursive(((R0If) e).getRetElse(), varList);
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
            }
//        if(e instanceof R0)
        System.out.println("error interpreting R0 expression");
        return null;
    }
}
