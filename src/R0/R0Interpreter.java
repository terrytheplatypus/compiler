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
public class R0Interpreter {
    
    public static int R0Interpret(R0Program p) {
        return ExpressionInterpret(p.getExp());
    }
    
    public static int ExpressionInterpret(R0Expression e){
        Map <String, Integer> varList = new HashMap<>();
        return ExpressionInterpretRecursive(e,varList);
    }
    public static int ExpressionInterpretRecursive(R0Expression e, Map <String, Integer> varList){
        //int case
        if(e instanceof R0Int) {
            //System.out.println("Int");
            return ((R0Int) e).getVal();
        }
        //var case
        if(e instanceof R0Var){
            //System.out.println("Var");
            if(varList.get( ((R0Var) e).getName() )==null) {
                System.err.println("Error:variable used before declared");
            }
            return varList.get( ((R0Var) e).getName() );
        }
        //negate case
        if(e instanceof R0Neg){
            //System.out.println("Neg");
            
            return -(ExpressionInterpretRecursive(((R0Neg) e).getChild(), varList));
        }
        //add case
        if(e instanceof R0Add) {
            //System.out.println("Add");
            List <R0Expression> l = ((R0Add) e).getChildren();
            return ExpressionInterpretRecursive(l.get(0), varList) + ExpressionInterpretRecursive(l.get(1), varList);
            
        }
        //let case
        if(e instanceof R0Let) {
            //System.out.println("Let");
            List <R0Expression> l = ((R0Let) e).getChildren();
            R0Var v = (R0Var) l.get(0);
            //System.out.println("assign to x");
            int xe = ExpressionInterpretRecursive(l.get(1), varList);
            varList.put(v.getName(), xe);
            //System.out.println("body expression");
            int be = ExpressionInterpretRecursive(l.get(2), varList);
            return be;
        }
        
        if(e instanceof R0Read) {
            System.out.println("Enter a value:");
            return (new Scanner(System.in)).nextInt();
        }
        System.out.println("error interpreting R0 expression");
        return 0;
    }
}
