/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package R0;

import static R0.ConciseConstructors.*;
import static R0.R3Type.R3Bool;
import static R0.R3Type.R3Int;
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
    static final List<R0Vector> vectors = new ArrayList<>();
    
    public static Map <String, Integer > globalVals = globalValInit();
    public static Map <String, Integer > globalValInit () {
        Map <String, Integer> ret = new HashMap<>();
        ret.put("free_ptr", 20);
        ret.put("fromspace_end", 4000);
        return ret;
    }

    //both the new R0Interpret and the ExpressionInterpret and the recursive
    //helper should return R0Literal (either boolean or int)
    public static R0Basic R3Interpret(R0Program p) throws Exception {
        return ExpressionInterpret(p.getExp());
    }

    public static R0Basic R3Interpret(R3TypedProgram p) throws Exception {
        return ExpressionInterpret(p.getExp());
    }

    public static R0Basic ExpressionInterpret(R0Expression e) throws Exception {
        if (e instanceof R3TypedExpr) {
            e = ((R3TypedExpr) e).getE();
        }
        Map<String, R0Basic> varList = new HashMap<>();
        return ExpressionInterpretRecursive(e, varList);
    }

    public static R0Basic ExpressionInterpretRecursive(R0Expression e, Map<String, R0Basic> varList) throws Exception {

        if (e instanceof R3TypedExpr) {
            e = ((R3TypedExpr) e).getE();
        }

        if(e instanceof R3GlobalValue) {
            return new R0Int(globalVals.get(((R3GlobalValue) e).getName())) ;
        }
        
        //int case
        if (e instanceof R0Int) {
            //System.out.println("Int");
            return (R0Int) e;
        }
        //literal bool case
        if (e instanceof R0LitBool) {
            return (R0LitBool) e;
        }
        if (e instanceof R0Vector) {
            List<R0Expression> newExps = new ArrayList<>();

            for (R0Expression exp : ((R0Vector) e).getElmts()) {
                newExps.add(ExpressionInterpretRecursive(exp, varList));
            }
            synchronized (vectors) {
                vectors.add(new R0Vector(newExps));
            }
            int index = vectors.size() - 1;
            //this gives you the actual instance of the vector
            return vectors.get(index);
        } //var case
        else if (e instanceof R0Var) {
            //System.out.println("Var");
            if (varList.get(((R0Var) e).getName()) == null) {
                System.err.println("Interpreter error:variable: " +((R0Var) e).getName() +
                        " used before declared");
            }
            return varList.get(((R0Var) e).getName());
        } //negate case
        else if (e instanceof R0Neg) {
            //System.out.println("Neg");
            //if R0Neg is being performed, it assumes
            //e's value is int, if it isn't then it will throw error
            //thus, Java itself does type checking, limiting the amount
            // of type checking that I need to actually do
            R0Int init = (R0Int) ExpressionInterpretRecursive(((R0Neg) e).getChild(), varList);

            return new R0Int(-init.getVal());
        } //add case
        else if (e instanceof R0Add) {
            //System.out.println("Add");
            List<R0Expression> l = ((R0Add) e).getChildren();
            int sum = ((R0Int) ExpressionInterpretRecursive(l.get(0), varList)).getVal() + ((R0Int) ExpressionInterpretRecursive(l.get(1), varList)).getVal();
            return new R0Int(sum);

        } //let case
        else if (e instanceof R0Let) {
            //System.out.println("Let");
            List<R0Expression> l = ((R0Let) e).getChildren();
            R0Var v;
            if(l.get(0)  instanceof R3TypedExpr) {
                 v = (R0Var) ((R3TypedExpr) l.get(0)).getE();
            } else {
                 v = (R0Var) l.get(0);
            }
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
        } else if (e instanceof R0Read) {
            //either assume that read still only takes ints,
            // or let it take bool as well
            System.out.println("Enter a value:");
            return new R0Int((new Scanner(System.in)).nextInt());
        } //this is for the comparison
        else if (e instanceof R0Cmp) {
            //get literal value of lhs, literal value of rhs, compare
            R0Basic lhs = ExpressionInterpretRecursive(((R0Cmp) e).getA(), varList);
            R0Basic rhs = ExpressionInterpretRecursive(((R0Cmp) e).getB(), varList);
            R0CmpOp op = ((R0Cmp) e).getOp();

            if (!lhs.getClass().equals(rhs.getClass())) {
                throw new Exception("oops");
            }
            //this next part should throw exception if there's type mismatch
            if (op instanceof R0Eq) {
                return new R0LitBool(lhs.equals(rhs));
            } else if ((lhs instanceof R0Int) && rhs instanceof R0Int) {
                int lhsVal = ((R0Int) lhs).getVal();
                int rhsVal = ((R0Int) rhs).getVal();
                if (op instanceof R0Gr) {
                    return new R0LitBool(lhsVal > rhsVal);
                } else if (op instanceof R0GrEq) {
                    return new R0LitBool(lhsVal >= rhsVal);
                } else if (op instanceof R0Less) {
                    return new R0LitBool(lhsVal < rhsVal);
                } else if (op instanceof R0LessEq) {
                    return new R0LitBool(lhsVal <= rhsVal);
                }
            } else {
                System.err.println("Comparison error");
            }
        } else if (e instanceof R0Not) {
            boolean b = ((R0LitBool) ExpressionInterpretRecursive(((R0Not) e).getX(), varList)).getVal();
            return new R0LitBool(!b);
        } else if (e instanceof R0If) {
            boolean flag = ((R0LitBool) ExpressionInterpretRecursive(((R0If) e).getCond(), varList)).getVal();
            if (flag) {
                R0Basic ret = ExpressionInterpretRecursive(((R0If) e).getRetIf(), varList);
//                    if(ret instanceof R0Int) {
//                        return (R0Int) ret;
//                    } else if(ret instanceof R0LitBool) {
//                        return (R0LitBool) ret;
//                    }
                return ret;
            } else {

                R0Basic ret = ExpressionInterpretRecursive(((R0If) e).getRetElse(), varList);
//                    if(ret instanceof R0Int) {
//                        return (R0Int) ret;
//                    } else if(ret instanceof R0LitBool) {
//                        return (R0LitBool) ret;
//                    }
                return ret;
            }

        } else if (e instanceof R0And) {
            R0LitBool a = (R0LitBool) ExpressionInterpretRecursive(((R0And) e).getA(), varList);
            if (a.getVal() == false) {
                return new R0LitBool(false);
            }
            R0LitBool b = (R0LitBool) ExpressionInterpretRecursive(((R0And) e).getB(), varList);
            boolean result = a.getVal() && b.getVal();
            return new R0LitBool(result);
        } else if (e instanceof R0VecSet) {
            //first argument can technically either be a vector or a variable,
            //but bcuz it doesn't return anything
            // it would only be useful for variables
            
            //if it's interpreting an R3TypedProgram then it needs to see
            //the expression inside the first argument
            
            R0Expression arg1 = ((R0VecSet) e).getVec();
            if(arg1 instanceof R3TypedExpr) {
                arg1 = ((R3TypedExpr) arg1).getE();
            }
            
            R0Var var = (R0Var) arg1;
            synchronized (vectors) {
                R0Vector vec = (R0Vector) varList.get(var.getName());
                //this should work because it's returning a reference
                int index = ((R0VecSet) e).getIndex().getVal();
                R0Expression newVal = ((R0VecSet) e).getNewVal();
                vec.getElmts().set(index, newVal);

            }
            return new R0Void();
        } else if (e instanceof R0VecRef) {
            
            //if it's interpreting an R3TypedProgram then it needs to see
            //the expression inside the first argument
            
            R0Expression arg1 = ((R0VecRef) e).getVec();
            if(arg1 instanceof R3TypedExpr) {
                arg1 = ((R3TypedExpr) arg1).getE();
            }
            
            R0Vector vec;
            
            if (arg1 instanceof R0Var) {
                R0Var var = (R0Var) arg1;
                vec = (R0Vector) varList.get(var.getName());
            } else {
                //if it's not a variable it's a literal vector
                //if it's not a vector then it errors out
                vec = (R0Vector) ExpressionInterpret((arg1));
            }
            if (vec == null) {
                System.err.println("sdfsd");
            }
            R0Expression ret = vec.getElmts().get(((R0VecRef) e).getIndex().getVal());
            R0Basic val = ExpressionInterpretRecursive(ret, varList);
            return val;
        } else if (e instanceof  R3Allocate) {
            
            //allocate returns a vector
            
            //for each element of the given type list,
            
            //return new R0Void();
            return vecAlloc(((R3Allocate) e).getType());
            
        } else if( e instanceof  R3Collect) {
            return new R0Void();
        }
//        if(e instanceof R0)
        System.out.println("error interpreting R0 expression");
        return null;
    }
    
    private static R0Vector vecAlloc (R3Type type) {
        
        List <R0Expression> newVecElmts = new ArrayList<>();
        for(R3Type curr:type.getElmtTypes()) {
            if(curr.isInt()) newVecElmts.add(new R3TypedExpr(nInt(0), R3Int()) );
            if(curr.isBool()) newVecElmts.add(new R3TypedExpr(nLitBool(true), R3Bool()));
            if(curr.isVec()) newVecElmts.add(new R3TypedExpr(vecAlloc(curr), curr));
        }
        return new R0Vector (newVecElmts);
    }
    
    //need separate methods for going through references to get and set the actual
    //vectors, as opposed to 
//    
//    
//     public static String R3Print(R0Program p) throws Exception {
//        return ExpressionPrint(p.getExp());
//    }
//
//    public static String R3Print(R3TypedProgram p) throws Exception {
//        return ExpressionPrint(p.getExp());
//    }
//
//    public static String ExpressionPrint(R0Expression e) throws Exception {
//        if (e instanceof R3TypedExpr) {
//            e = ((R3TypedExpr) e).getE();
//        }
//        Map<String, R0Basic> varList = new HashMap<>();
//        return ExpressionPrintRecursive(e, varList, 0);
//    }
//
//    public static String ExpressionPrintRecursive(R0Expression e, Map<String, R0Basic> varList, 
//            int depth) throws Exception {
//
//        if (e instanceof R3TypedExpr) {
//            e = ((R3TypedExpr) e).getE();
//        }
//
//        if(e instanceof R3GlobalValue) {
//            return ((R3GlobalValue) e).getName();
//        }
//        
//        //int case
//        if (e instanceof R0Int) {
//            //System.out.println("Int");
//            return ((R0Int) e).stringify();
//        }
//        //literal bool case
//        if (e instanceof R0LitBool) {
//            return ((R0LitBool) e).stringify();
//        }
//        if (e instanceof R0Vector) {
//            List<R0Expression> newExps = new ArrayList<>();
//
//            for (R0Expression exp : ((R0Vector) e).getElmts()) {
//                newExps.add(ExpressionInterpretRecursive(exp, varList));
//            }
//            synchronized (vectors) {
//                vectors.add(new R0Vector(newExps));
//            }
//            int index = vectors.size() - 1;
//            //this gives you the actual instance of the vector
//            return vectors.get(index);
//        } //var case
//        else if (e instanceof R0Var) {
//            //System.out.println("Var");
//            if (varList.get(((R0Var) e).getName()) == null) {
//                System.err.println("Interpreter error:variable: " +((R0Var) e).getName() +
//                        " used before declared");
//            }
//            return varList.get(((R0Var) e).getName());
//        } //negate case
//        else if (e instanceof R0Neg) {
//            //System.out.println("Neg");
//            //if R0Neg is being performed, it assumes
//            //e's value is int, if it isn't then it will throw error
//            //thus, Java itself does type checking, limiting the amount
//            // of type checking that I need to actually do
//            R0Int init = (R0Int) ExpressionInterpretRecursive(((R0Neg) e).getChild(), varList);
//
//            return new R0Int(-init.getVal());
//        } //add case
//        else if (e instanceof R0Add) {
//            //System.out.println("Add");
//            List<R0Expression> l = ((R0Add) e).getChildren();
//            int sum = ((R0Int) ExpressionInterpretRecursive(l.get(0), varList)).getVal() + ((R0Int) ExpressionInterpretRecursive(l.get(1), varList)).getVal();
//            return new R0Int(sum);
//
//        } //let case
//        else if (e instanceof R0Let) {
//            //System.out.println("Let");
//            List<R0Expression> l = ((R0Let) e).getChildren();
//            R0Var v = (R0Var) l.get(0);
//            //System.out.println("assign to x");
//            R0Basic xe = ExpressionInterpretRecursive(l.get(1), varList);
//            varList.put(v.getName(), xe);
//
//            //if it's a vector put it in the vectorVals map
////            if(xe instanceof R0Vector) {
////                synchronized(vectors) {
////                    //vectorVals.put(v, (R0Vector) xe);
////                    vectors.add((R0Vector) xe);
////                    varList.put(v.getName(), xe);
////                }
////            }
//            //System.out.println("body expression");
//            R0Basic be = ExpressionInterpretRecursive(l.get(2), varList);
//            return be;
//        } else if (e instanceof R0Read) {
//            //either assume that read still only takes ints,
//            // or let it take bool as well
//            System.out.println("Enter a value:");
//            return new R0Int((new Scanner(System.in)).nextInt());
//        } //this is for the comparison
//        else if (e instanceof R0Cmp) {
//            //get literal value of lhs, literal value of rhs, compare
//            R0Basic lhs = ExpressionInterpretRecursive(((R0Cmp) e).getA(), varList);
//            R0Basic rhs = ExpressionInterpretRecursive(((R0Cmp) e).getB(), varList);
//            R0CmpOp op = ((R0Cmp) e).getOp();
//
//            if (!lhs.getClass().equals(rhs.getClass())) {
//                throw new Exception("oops");
//            }
//            //this next part should throw exception if there's type mismatch
//            if (op instanceof R0Eq) {
//                return new R0LitBool(lhs.equals(rhs));
//            } else if ((lhs instanceof R0Int) && rhs instanceof R0Int) {
//                int lhsVal = ((R0Int) lhs).getVal();
//                int rhsVal = ((R0Int) rhs).getVal();
//                if (op instanceof R0Gr) {
//                    return new R0LitBool(lhsVal > rhsVal);
//                } else if (op instanceof R0GrEq) {
//                    return new R0LitBool(lhsVal >= rhsVal);
//                } else if (op instanceof R0Less) {
//                    return new R0LitBool(lhsVal < rhsVal);
//                } else if (op instanceof R0LessEq) {
//                    return new R0LitBool(lhsVal <= rhsVal);
//                }
//            } else {
//                System.err.println("Comparison error");
//            }
//        } else if (e instanceof R0Not) {
//            boolean b = ((R0LitBool) ExpressionInterpretRecursive(((R0Not) e).getX(), varList)).getVal();
//            return new R0LitBool(!b);
//        } else if (e instanceof R0If) {
//            boolean flag = ((R0LitBool) ExpressionInterpretRecursive(((R0If) e).getCond(), varList)).getVal();
//            if (flag) {
//                R0Basic ret = ExpressionInterpretRecursive(((R0If) e).getRetIf(), varList);
////                    if(ret instanceof R0Int) {
////                        return (R0Int) ret;
////                    } else if(ret instanceof R0LitBool) {
////                        return (R0LitBool) ret;
////                    }
//                return ret;
//            } else {
//
//                R0Basic ret = ExpressionInterpretRecursive(((R0If) e).getRetElse(), varList);
////                    if(ret instanceof R0Int) {
////                        return (R0Int) ret;
////                    } else if(ret instanceof R0LitBool) {
////                        return (R0LitBool) ret;
////                    }
//                return ret;
//            }
//
//        } else if (e instanceof R0And) {
//            R0LitBool a = (R0LitBool) ExpressionInterpretRecursive(((R0And) e).getA(), varList);
//            if (a.getVal() == false) {
//                return new R0LitBool(false);
//            }
//            R0LitBool b = (R0LitBool) ExpressionInterpretRecursive(((R0And) e).getB(), varList);
//            boolean result = a.getVal() && b.getVal();
//            return new R0LitBool(result);
//        } else if (e instanceof R0VecSet) {
//            //first argument can technically either be a vector or a variable,
//            //but bcuz it doesn't return anything
//            // it would only be useful for variables
//            
//            //if it's interpreting an R3TypedProgram then it needs to see
//            //the expression inside the first argument
//            
//            R0Expression arg1 = ((R0VecSet) e).getVec();
//            if(arg1 instanceof R3TypedExpr) {
//                arg1 = ((R3TypedExpr) arg1).getE();
//            }
//            
//            R0Var var = (R0Var) arg1;
//            synchronized (vectors) {
//                R0Vector vec = (R0Vector) varList.get(var.getName());
//                //this should work because it's returning a reference
//                int index = ((R0VecSet) e).getIndex().getVal();
//                R0Expression newVal = ((R0VecSet) e).getNewVal();
//                vec.getElmts().set(index, newVal);
//
//            }
//            return new R0Void();
//        } else if (e instanceof R0VecRef) {
//            
//            //if it's interpreting an R3TypedProgram then it needs to see
//            //the expression inside the first argument
//            
//            R0Expression arg1 = ((R0VecRef) e).getVec();
//            if(arg1 instanceof R3TypedExpr) {
//                arg1 = ((R3TypedExpr) arg1).getE();
//            }
//            
//            R0Vector vec;
//            
//            if (arg1 instanceof R0Var) {
//                R0Var var = (R0Var) arg1;
//                vec = (R0Vector) varList.get(var.getName());
//            } else {
//                //if it's not a variable it's a literal vector
//                //if it's not a vector then it errors out
//                vec = (R0Vector) ExpressionInterpret((arg1));
//            }
//            if (vec == null) {
//                System.err.println("sdfsd");
//            }
//            R0Expression ret = vec.getElmts().get(((R0VecRef) e).getIndex().getVal());
//            R0Basic val = ExpressionInterpretRecursive(ret, varList);
//            return val;
//        } else if (e instanceof  R3Allocate) {
//            return new R0Void();
//            
//        } else if( e instanceof  R3Collect) {
//            return new R0Void();
//        }
////        if(e instanceof R0)
//        System.out.println("error interpreting R0 expression");
//        return null;
//    }
    
    
}
