/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilerPasses;

import X0.*;
import X1.X1negq;
import X1.X1Instr;
import X1.X1callq;
import X1.X1movq;
import X1.X1Var;
import X1.X1retq;
import X1.X1Arg;
import X1.X1addq;
import X1.X1Reg;
import X1.X1Int;
import X1.X1Program;
import C0.*;
import C0.C0Cmp.opValue;
import R0.*;
import static R0.ConciseConstructors.*;
import X1.AdjacencyMap;
import static X1.ArgConversion.C0ToX1Arg;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javafx.util.Pair;
import static C0.C0Cmp.*;
import static C0.C0Cmp.opValue.EQ;
import X0.X0ByteReg;
import X0.X0If;
import X0.X0cmpq;
import X0.X0jmpif;
import X0.X0label;
import X0.X0set;
import X0.X0xorq;
import X1.X1ByteReg;
import X1.X1If;
import X1.X1TypedProgram;
import X1.X1set;
import X1.X1cmpq;
import X1.X1movzbq;
import static X1.X1set.R0CmpOpToCC;
import X1.X1set.conditionCode;
import X1.X1xorq;

/**
 *
 * @author david
 */
public class PassMethods {
    //numUniqueVars is used in the uniquify and flatten passes
    
    //because numUniqueVars is static this method can't be used concurrently but I'm
    //not using it concurrently so it doesn't matter
    //THOUGH this also means that i can't call uniquify on multiple programs
    //then flatten the results
    private static int numUniqueVars = 0;
    
    public static String [] regNames = {"r8", "r9", "r10", "r11",
                                        "rcx", "rdx", /*"rdi",*/ "rsi"};
    
    public static R2TypedProgram uniquify(R2TypedProgram p) {
        
        return new R2TypedProgram(uniquify(p.getProg()), p.getType());
    }
    //all static variables need to be reset when compile is called on a new program,
    //and because uniquify is the first step, it should set numUniqueVars to 0
    public static R0Program uniquify(R0Program p) {
        numUniqueVars = 0;
        return new R0Program(uniquifyRecursive(p.getExp(), new HashMap<>()));
    }
    
    //very bad space-wise, optimization would be to make a static array of maps,
    //and pass the index of the new map in the uniquifyRec call after doing something
    //w/ let expre
    //this whole function would be a lot more compact in a functional language
    private static R0Expression uniquifyRecursive(R0Expression e, Map<String, String> varNameList) {
        
            if(e instanceof R0Int) {
                return e;
            }
            else if(e instanceof R0Var) {
                String varName = varNameList.get( ((R0Var) e).getName() );
                return nVar(varName);
            }
            else if(e instanceof R0Read) {
                return e;
            }
        
        else if(e instanceof R0Let) {
            List <R0Expression> exps = e.getChildren();
            R0Var x = (R0Var) exps.get(0);
            varNameList.put(x.getName(), x.getName()+"_"+String.valueOf(numUniqueVars++));
            String newVarName = varNameList.get(x.getName());
            R0Var newVar = nVar(newVarName);
            return nLet(newVar, 
                    uniquifyRecursive(exps.get(1), varNameList),
                    uniquifyRecursive(exps.get(2), varNameList));
        }
        else if(e instanceof R0Neg) {
            return nNeg(uniquifyRecursive(((R0Neg) e).getChild(),varNameList));
        }
        else if(e instanceof R0Add) {
            List<R0Expression> cs = e.getChildren();
            return nAdd(uniquifyRecursive(cs.get(0),varNameList),
                    uniquifyRecursive(cs.get(1),varNameList));
        }
        //adding if/else stuff
        else if(e instanceof R0If) {
            List<R0Expression> cs = e.getChildren();
            return nIf(uniquifyRecursive(cs.get(0), varNameList),
                    uniquifyRecursive(cs.get(1), varNameList),
                    uniquifyRecursive(cs.get(2), varNameList));
        } else if(e instanceof R0Cmp) {
            List<R0Expression> cs = e.getChildren();
            return nCmp(((R0Cmp) e).getOp(), 
                    uniquifyRecursive(cs.get(0), varNameList), 
                    uniquifyRecursive(cs.get(1), varNameList));
        } else if ( e instanceof R0Not) {
            List<R0Expression> cs = e.getChildren();
            return nNot(uniquifyRecursive(cs.get(0), varNameList));
        } if(e instanceof R0And) {
            List<R0Expression> cs = e.getChildren();
            return new R0And(uniquifyRecursive(cs.get(0), varNameList),
            uniquifyRecursive(cs.get(1), varNameList));
        }
        if(e instanceof R0LitBool) {
                return e;
        }
        
        else{
            return null; 
        }
    }
    
    /**
     * IIMPORTANT: C0Program is assumed to have no duplicate variable names
    /* so it should only be run with a uniquified R0Program,
    * also if run with un-uniquified program, it may run into name collisions
     * @param p
     * @return 
     */
    public static C0Program flatten(R0Program p){
        
        return flattenRecursive(p.getExp());
    }
    //R0 expression for func arg
    private static C0Program flattenRecursive(R0Expression e) {
        //C0Program: <vars, statements, arg>
        List <C0Var> vars =new ArrayList<>();
        List <C0Stmt> stmts = new ArrayList<>();
        C0Arg arg;
        //cases:
        //int: F(int) = < null, null, int>
        if(e instanceof R0Int) {
            return new C0Program(vars, stmts, new C0Int(((R0Int) e).getVal()));
        }
        //read <rv, (rv:=(read)), rv>
        else if(e instanceof R0Read) {
            C0Var rv = new C0Var("read_"+String.valueOf(numUniqueVars++));
            vars.add(rv);
            C0Assign assignRead = new C0Assign(rv, new C0Read());
            stmts.add(assignRead);
            return new C0Program(vars, stmts, rv);
        }
        //neg(e) ( let <vs, ss, earg> = F(e) -> <nv ++vs, ss++(:= nv(-earg)), nv>
        else if(e instanceof R0Neg) {
            C0Var nv = new C0Var("neg_"+String.valueOf(numUniqueVars++));
            C0Program fe = flattenRecursive(((R0Neg) e).getChild());
            vars.add(nv);
            vars.addAll(fe.getVarList());
            stmts.addAll(fe.getStmtList());
            stmts.add(new C0Assign(nv, new C0Neg(fe.getReturnArg())));
            return new C0Program(vars, stmts, nv);
        }
        //add F(e1) = <vs1, ss1, ea1>, F(e2) = <vs2, ss2, ea2> = F(e2)
        //<vs1++vs2++av, ss1++ss2++(av = (+ ea1 ea2)), av>
        else if(e instanceof R0Add) {
            C0Var av = new C0Var("addReturn_"+String.valueOf(numUniqueVars++));
            vars.add(av);
            C0Program fe1 = flattenRecursive(e.getChildren().get(0));
            C0Program fe2 = flattenRecursive(e.getChildren().get(1));
            vars.addAll(fe1.getVarList());
            vars.addAll(fe2.getVarList());
            stmts.addAll(fe1.getStmtList());
            stmts.addAll(fe2.getStmtList());
            stmts.add(new C0Assign(av, new C0Add(
                    fe1.getReturnArg(), 
                    fe2.getReturnArg())));
            return new C0Program(vars, stmts, av);
        }
        //var < {var}, null, var>
        else if(e instanceof R0Var) {
            vars.add(new C0Var(((R0Var)e)));
            return new C0Program(vars, stmts, new C0Var((R0Var)e));
        }
        //F(let x e1 e2) = <vs1 ++ vs2 ++ {x}, ss ++ (:= x ea1) ++ ss2, ea2>
        else if(e instanceof R0Let) {
            C0Var x = new C0Var( (R0Var) e.getChildren().get(0));
            C0Program fe1 = flattenRecursive(e.getChildren().get(1));
            C0Program fe2 = flattenRecursive(e.getChildren().get(2));
            vars.addAll(fe1.getVarList());
            vars.addAll(fe2.getVarList());
            vars.add(x);
            stmts.addAll(fe1.getStmtList());
            stmts.add(new C0Assign(x, fe1.getReturnArg()));
            stmts.addAll(fe2.getStmtList());
            arg = fe2.getReturnArg();
            return new C0Program(vars, stmts, arg);
        }
        
        //adding if/else stuff
        
        //here, AND is broken down into if statement
        //this makes writing the rest of the compiler less error-prone
        //because i have less cases to keep track of
        if(e instanceof R0And) {
            
            C0Var av = new C0Var("and_"+String.valueOf(numUniqueVars++));
            arg = av;
            C0Program fe1 = flattenRecursive(e.getChildren().get(0));
            C0Program fe2 = flattenRecursive(e.getChildren().get(1));
            vars.addAll(fe1.getVarList());
            vars.addAll(fe2.getVarList());
            
            stmts.addAll(fe1.getStmtList());
            stmts.addAll(fe2.getStmtList());
//            stmts.add(new C0Assign(av, 
//                    new C0And(fe1.getReturnArg(),fe2.getReturnArg())));
            //now u have to make a comparison between the first arg and second
            //if first is true, you return result of second, else you return false
            
           C0Cmp checkFirst = new C0Cmp(EQ, 
                        fe1.getReturnArg() , 
                        new C0LitBool(true));
           
           C0Stmt checkSecond = new C0Assign(av, fe2.getReturnArg());
           C0Stmt quickFail = new C0Assign(av,new C0LitBool(false));
           List <C0Stmt> cond2 = new ArrayList<>();
           List <C0Stmt> shortCircuitFalse = new ArrayList<>();
           cond2.add(checkSecond);
           shortCircuitFalse.add(quickFail);
           
           C0If deSugaredAnd = new C0If(checkFirst,cond2, shortCircuitFalse);
            stmts.add(deSugaredAnd);
            
            return new C0Program(vars, stmts, arg);
            
        }
        else if(e instanceof R0If) {
            
            boolean ifOnly = false;
            boolean elseOnly = false;
            
            List <C0Stmt> ifStmts = new ArrayList<>();
            
            List <C0Stmt> elseStmts = new ArrayList<>();
            List<R0Expression> cs = e.getChildren();
            //opValue op = e.getChildren().get(0)
            //if e is an instance of variable, then convert it to
            // (eq var TRUE)
            
            C0Cmp finalCmp = null;
            R0Expression cond1 = e.getChildren().get(0);
            if(!(cond1 instanceof R0Cmp)) {
                if(cond1 instanceof R0LitBool) {
                   //a small optimization could added here:
                   //if the literal bool is true, you only need to compile the if branch,
                   //and if the literal bool is false, you only need to compile the else branch.
                   //possibly unwise to add this in before getting it to work the normal way
                   //but i wanted to try it.
                   if(((R0LitBool) cond1).getVal()) ifOnly = true;
                   else elseOnly = true;
               } else {

//                    if(cond1 instanceof R0Var) {
//                   cond1 = nCmp(nEq(),cond1, nLitBool(true));
//                   }
                    C0Program flatCond = flattenRecursive(cond1);
                    vars.addAll(flatCond.getVarList());
                    stmts.addAll(flatCond.getStmtList());
                    //cond1 = nCmp(nEq(),cond1, nLitBool(true));
                    finalCmp = new C0Cmp(EQ, 
                        flatCond.getReturnArg() , 
                        new C0LitBool(true));
                }
            }
            
            if(!ifOnly && !elseOnly && (cond1 instanceof R0Cmp)) {
                
                //this part should not actually generate the C0Cmp itself,
                //the C0Cmp case should do that.this part should only compare the return value
                //of the comparison to true
                
                R0Cmp cmp = (R0Cmp) cond1;
//                opValue op = R0CmpOpToString(cmp.getOp() );
                C0Program cond2 = flattenRecursive(cond1);
                vars.addAll(cond2.getVarList());
                stmts.addAll(cond2.getStmtList());
//                R0Expression a = cmp.getA();
//                R0Expression b = cmp.getB();
//                
                finalCmp = new C0Cmp(EQ, 
                        cond2.getReturnArg() , 
                        new C0LitBool(true));
            }
            
            C0Program ifs = flattenRecursive(e.getChildren().get(1));
            C0Program elses = flattenRecursive(e.getChildren().get(2));
            
            //vars.addAll(elses.getVarList());
            
            //arg will be ini
            
            
            C0Var ifv = new C0Var("if_"+String.valueOf(numUniqueVars++));
            vars.add(ifv);
            arg = ifv;
            
            if(!elseOnly) {
                vars.addAll(ifs.getVarList());
                ifStmts.addAll(ifs.getStmtList());
                ifStmts.add(new C0Assign(ifv, ifs.getReturnArg()));
                
            }
            
            if(!ifOnly) {
                
                vars.addAll(elses.getVarList());
                elseStmts .addAll(elses.getStmtList());
                elseStmts.add(new C0Assign(ifv, elses.getReturnArg()));
            }
            
            //if neither ifOnly or elseOnly are true, add both to if statement
            //for now this will be true to make testing easier
            if(!ifOnly && !elseOnly) {
                stmts.add(new C0If(finalCmp, ifStmts, elseStmts));
            }
            //if elseOnly and not ifOnly add else to the thing
            
            
            return new C0Program(vars, stmts, arg);
            
        } else if(e instanceof R0Cmp) {
            C0Var cmpv = new C0Var("cmp_"+String.valueOf(numUniqueVars++));
            vars.add(cmpv);
            opValue op = R0CmpOpToString(((R0Cmp) e).getOp());
            List<R0Expression> cs = e.getChildren();
            C0Program a = flattenRecursive(((R0Cmp) e).getA());
            C0Program b = flattenRecursive(((R0Cmp) e).getB());
            vars.addAll(a.getVarList());
            vars.addAll(b.getVarList());
            stmts.addAll(a.getStmtList());
            stmts.addAll(b.getStmtList());
            arg = cmpv;
            stmts.add(new C0Assign(cmpv, 
                    new C0Cmp(op, a.getReturnArg(), b.getReturnArg()) ));
            return new C0Program(vars, stmts, arg);
            
        } else if ( e instanceof R0Not) {
            C0Var notv = new C0Var("not_"+String.valueOf(numUniqueVars++));
            arg = notv;
            List<R0Expression> cs = e.getChildren();
            C0Program x = flattenRecursive(((R0Not) e).getX());
            vars.addAll(x.getVarList());
            vars.add(notv);
            stmts.addAll(x.getStmtList());
            stmts.add(new C0Assign(notv, new C0Not(x.getReturnArg())));
            return new C0Program(vars, stmts, arg);
            
        }
         if(e instanceof R0LitBool) {
            return new C0Program(vars, stmts, new C0LitBool(((R0LitBool) e).getVal()));
         }
        
        return null;
    }
    
    public static C1TypedProgram flatten(R2TypedProgram p) {
        C0Program flat = flatten(new R0Program(p.getExp()));
        C1TypedProgram r = new C1TypedProgram(flat, p.getType());
        return r;
        
    }
    
    public static X1TypedProgram select(C1TypedProgram p) {
        return new X1TypedProgram(select(p.getP()), p.getType());
    }
    
    // assumptions made by select and assign pass: rax is only register used in select pass
    //this version of select pass assumes that all C0 statements are assignments
    public static X1Program select(C0Program p) {
        
        List <X1Var> vars = new ArrayList<>();
        List <X1Instr> instrs = new ArrayList<>();
         
        
        for(C0Stmt s1:p.getStmtList()) {
            if(s1 instanceof C0Assign) {
                C0Assign s = (C0Assign) s1;
                X1Var x = new X1Var(s.getX().getName());
                vars.add(x);
                C0Expression e = s.getExp();
                if(e instanceof C0Arg) {

                    //movq arg x
                    instrs.add(new X1movq(C0ToX1Arg((C0Arg)e), C0ToX1Arg(s.getX())));
                } else if(e instanceof C0Read) {
                    //callq _read
                    instrs.add(new X1callq("readint"));
                    //movq rax x
                    instrs.add(new X1movq(new X1Reg(), x));

                } else if(e instanceof C0Add) {
                    //movq arg1 x
                    instrs.add(new X1movq(C0ToX1Arg(((C0Add) e).getA()), x));
                    //addq arg2 x
                    instrs.add(new X1addq(C0ToX1Arg(((C0Add) e).getB()), x));

                } else if(e instanceof C0Neg) {
                    //movq arg x
                    instrs.add(new X1movq(C0ToX1Arg(((C0Neg) e).getA()), x));
                    //negq x
                    instrs.add(new X1negq(x));
                }
                
                //add C0Cmp case and C0Not case
                else if(e instanceof C0Cmp) {
                    //cmpq a b
                    X1Arg a = C0ToX1Arg(((C0Cmp) e).getA());
                    X1Arg b = C0ToX1Arg(((C0Cmp) e).getB());
                    instrs.add(new X1cmpq(b,
                            a));
                    //set e (bytereg) al
                    conditionCode code = R0CmpOpToCC(((C0Cmp) e).getOp());
                    instrs.add(new X1set(code, new X1ByteReg()));
                    //movzbq al lhs
                    instrs.add(new X1movzbq(new X1ByteReg(), x));
                    
                } else if(e instanceof C0Not) {
                    instrs.add(new X1xorq(new X1Int(1), C0ToX1Arg(((C0Not) e).getX())));
                    instrs.add(new X1movq(C0ToX1Arg(((C0Not) e).getX()), x));
                }
                //right now i changed flatten pass to get rid of C0And so i don't have
                //to add another instruction
                //this might be a little less compact than just adding an "and" operation
                //but it makes it so i have to look at less cases.
                //i could do the same with Not tbh, except it was already suggested to
                //use xorq
                

            } else if(s1 instanceof C0If) {
                
                //get statements from if and else and turn those into C0Programs,
                //recursively do select on them
                List <C0Stmt> ifs = ((C0If) s1).getIfStmts();
                List <C0Stmt> elses = ((C0If) s1).getElseStmts();
                
                //you could make a new method to extract variables from a list of C0Stmts
                //but it's easier to pass all vars and only 
                //add the new variables it gets to the list
                
                //the temporary C0 program is given arbitrary return argument that's not actually
                //in the final program
                //2 copies because otherwise it uses it by reference
                List ifsVarList = new ArrayList(vars);
                List elsesVarList = new ArrayList(vars);
                
                C0Program ifBranch = new C0Program(ifsVarList, ifs, new C0Int(-1));
                C0Program elseBranch = new C0Program(elsesVarList, elses, new C0Int(-1));
                X1Program flatIf = select(ifBranch);
                X1Program flatElse = select(elseBranch);
                //you have to remove the unused return from both branches
                flatIf.getInstrList().remove(flatIf.getInstrList().size()-1);
                flatElse.getInstrList().remove(flatElse.getInstrList().size()-1);
                
                ifsVarList.removeAll(vars);
                elsesVarList.removeAll(vars);
                vars.addAll(ifsVarList);
                vars.addAll(elsesVarList);
                
                //because the flatten step only has comparison for equality inside
                //the C0 ifs, you just need to get the first argument
                C0Arg ifCmp = ((C0If) s1).getCond().getA();
                
                instrs.add(new X1If(C0ToX1Arg(ifCmp), 
                        flatIf.getInstrList(), 
                        flatElse.getInstrList()));
                
            }
        }
        X1Arg ret = C0ToX1Arg(p.getReturnArg());
        /*put here instead of assign: mov arg into rax,
        then  put rax into rdi,
        then call printint,
        then at X1retq (no arguments)*/
        //or if you keep it as retq(ret) then treat retq like a move to rax
        instrs.add( new X1retq(ret));
        return new X1Program(vars, instrs, ret);
    }
    public static X1TypedProgram uncoverLive(X1TypedProgram p) {
        return new X1TypedProgram(uncoverLive(p.getProg()), p.getType());
    }
     public static X1Program uncoverLive(X1Program p) {
         return uncoverLive(p, null);
     }
    public static X1Program uncoverLive(X1Program p, List <X1Var> initLivBefs) {
       List <X1Instr> instrs = p.getInstrList();
       //make list for both live after sets and live before sets
       List <List <X1Var>> livAfs = new ArrayList<>(Collections.nCopies(instrs.size(), new ArrayList <X1Var>() ));
       List <List <X1Var>> livBefs = new ArrayList<>(Collections.nCopies(instrs.size(), new ArrayList <X1Var>() ));
        ListIterator <X1Instr> it = instrs.listIterator(instrs.size());
        
        /*
        List <X1Var> W_k= new ArrayList<>();
        List <X1Var> R_k= new ArrayList<>();
        List <X1Var> L_b_k1= new ArrayList<>();
        List <X1Var> L_a_k= new ArrayList<>();
        List <X1Var> L_b_k= new ArrayList<>();
        */
        
        //next is to put the live after lists in correct index
        //int n = instrs.size();
        for(int n = instrs.size()-1; n >= 0; n--) {
            
            List <X1Var> W_k= new ArrayList<>();
            List <X1Var> R_k= new ArrayList<>();
            
            //n--;
            X1Instr i = instrs.get(n);
            //X1Instr i = it.previous();
            //first comput wk and rk based on the current instruction
            
            //some of the following variable changes may be redundant but
            //are made so it would be easier to implement
            
            //l_a_k = l_b_k1
            
            //l_b_k = (l_a_k - w_k)U r_k
            
            // so l_b_k = (l_b_k+1 - w_k)U r_k
            if(i instanceof X1addq) {
                X1Arg a = ((X1addq) i).getA();
                X1Arg b = ((X1addq) i).getB();
                if(a instanceof X1Var) {
                    R_k.add((X1Var)a);
                }
                if(b instanceof X1Var) {
                    W_k.add((X1Var)b);
                    R_k.add((X1Var)b);
                }
            } else if(i instanceof X1movq) {
                X1Arg a = ((X1movq) i).getA();
                X1Arg b = ((X1movq) i).getB();
                if(a instanceof X1Var) {
                    R_k.add((X1Var)a);
                }
                if(b instanceof X1Var) {
                    W_k.add((X1Var)b);
                }
            } else if(i instanceof X1negq) {
                X1Arg x = ((X1negq) i).getX();
                if(x instanceof X1Var) {
                    W_k.add((X1Var)x);
                    R_k.add((X1Var)x);
                }
            } else if(i instanceof X1retq) {
                //X1retq currently treated as mov
                X1Arg x = ((X1retq) i).getX();
                if(x instanceof X1Var) {
                    R_k.add((X1Var)x);
                }
            }
            //adding in stuff for control flow
            else if (i instanceof X1cmpq) {
                X1Arg a = ((X1cmpq) i).getA();
                X1Arg b = ((X1cmpq) i).getB();
                if(a instanceof X1Var) {
                    R_k.add((X1Var) a);
                }
                if(b instanceof X1Var) {
                    R_k.add((X1Var) b);
                }
            } else if (i instanceof X1movzbq) {
                X1Arg a = ((X1movzbq) i).getA();
                X1Arg b = ((X1movzbq) i).getB();
                if(a instanceof X1Var) {
                    R_k.add((X1Var) a);
                }
                if(b instanceof X1Var) {
                    R_k.add((X1Var) b);
                }
            } else if (i instanceof X1xorq) {
                X1Arg a = ((X1xorq) i).getA();
                X1Arg b = ((X1xorq) i).getB();
                if(a instanceof X1Var) {
                    R_k.add((X1Var) a);
                }
                if(b instanceof X1Var) {
                    R_k.add((X1Var) b);
                }
            } else if (i instanceof X1set) {
                //unnecessary to add stuff here because you only
                //use set with al
            }
            
            
            //next set of if-else to computer live before and live after
            if( n == instrs.size()-1) {
                if(initLivBefs == null) {
                livBefs.set(n, R_k);
                } else {
                    List <X1Var> currLivBefs = new ArrayList<>();
                    currLivBefs.addAll(initLivBefs);
                    currLivBefs.addAll(R_k);
                    livBefs.set(n, currLivBefs);
                }
               // continue;
            }  else if (i instanceof X1If) {
                X1Var cond = (X1Var) ((X1If) i).getCond();
                livAfs.set(n, livBefs.get(n+1));
                List <X1Var> lak = new ArrayList( livAfs.get(n));
                
                X1Program ifs = new X1Program(new ArrayList<>(),
                        ((X1If) i).getIfs(),
                        new X1Int(-1));
                X1Program elses = new X1Program(new ArrayList<>(),
                        ((X1If) i).getElses(),
                        new X1Int(-1));
                
                
                
                //combine the live sets of the if child  and else child into one set
                
                X1Program ifsWithLiveAfters = uncoverLive(ifs);
                X1Program elsesWithLiveAfters = uncoverLive(elses);
                
                
                //this should change the liveness sets in the if instruction, but might not,
                //so debug it
                ((X1If) i).setIfLivAfs(ifsWithLiveAfters.getLiveAfters());
                ((X1If) i).setElseLivAfs(elsesWithLiveAfters.getLiveAfters());
                
                
                List <X1Var> allIfLives = new ArrayList <>();
                for(List <X1Var> curr: ifsWithLiveAfters.getLiveAfters()) {
                    allIfLives.addAll(curr);
                }
                for(List <X1Var> curr: elsesWithLiveAfters.getLiveAfters()) {
                    allIfLives.addAll(curr);
                }
                //because of how "if" compilation is implemented, 
                //you know that it's a variable compared against true
                allIfLives.add(cond);
                
                allIfLives.addAll(lak);
                livBefs.set(n, allIfLives);
                
                
            } else {
                
                livAfs.set(n, livBefs.get(n+1));
                //live after k
                List <X1Var> lak = new ArrayList( livAfs.get(n));
                lak.removeAll(W_k);
                //putting it into hashset and back so it can add without duplcates
                Set <X1Var> diff = new HashSet(lak);
                Set <X1Var> r = new HashSet(R_k);
                diff.addAll(r);
                List result = new ArrayList(diff);
                livBefs.set(n, result);
            }
            
            // so l_b_k = (l_b_k+1 - w_k)U r_k
            
        }
        return new X1Program(p.getVarList(), instrs, p.getRetArg(), livAfs);
    }
    
    public static X1TypedProgram buildInterference(X1TypedProgram p) {
        return new X1TypedProgram(buildInterference(p.getProg()), p.getType());
    }
    
    public static X1Program buildInterference(X1Program p) {
        
        X1Program newProg;
        AdjacencyMap map = new AdjacencyMap();
        List <X1Reg> registers = new ArrayList<>();
        for(String r:regNames) registers.add(new X1Reg(r));
        
        //put empty values in the map for each variable
        for(X1Var v:p.getVarList()){
            map.addVertex(v);
        }
        
        //for xinstr's corresponding liveafter
        int n = -1;
        for(X1Instr i:p.getInstrList()) {
            n++;
            if(i instanceof X1addq) {
                X1Arg s = ((X1addq) i).getA();
                X1Arg d = ((X1addq) i).getB();
                for( X1Var v:p.getLiveAfters().get(n)) {
                    if(!v.equals((X1Var) d)) {
                        map.addEdge(d, (X1Var)v );
                    }
                }
                
            } else if(i instanceof X1movq) {
                X1Arg s = ((X1movq) i).getA();
                X1Arg d = ((X1movq) i).getB();
                for( X1Var v:p.getLiveAfters().get(n)) {
                    //next line is just to patch if it isn't var
                    if(d instanceof X1Int) continue;
                    if(s instanceof X1Int) s = new X1Var("_");
                    
                    
                    if(!v.equals((X1Arg) s) && !v.equals((X1Arg) d)) {
                        map.addEdge(d, (X1Var)v );
                    }
                }
            } else if(i instanceof X1negq) {
                X1Arg d = ((X1negq) i).getX();
                if(d instanceof X1Int) continue;
                for( X1Var v:p.getLiveAfters().get(n)) {
                    if(!v.equals((X1Var) d)) {
                        map.addEdge(d, (X1Var)v );
                    }
                }
                
            } else if(i instanceof X1callq) {
//                X1Arg x = ((X1callq) i).getX();
//                if(x instanceof X1Var) {
                    //will implement after i implement the rest of it
                    for( X1Var v:p.getLiveAfters().get(n)) {
                        registers.forEach((r) -> { 
                            map.addEdge(r, v);
                        });
                    }
                    
                //}
            } else if (i instanceof X1retq) {
                //X1retq currently treated as mov
                //but i don't think it has to do anything
                //because it's last so there's no liveafter set
                /*
                for( X1Var v:p.getLiveAfters().get(n)) {
                    if(v != (X1Var) s && v != (X1Var) d) {
                        map.addEdge(d, (X1Var)v );
                    }
                }
                */
            } else if (i instanceof X1xorq) {
                X1Arg s = ((X1xorq) i).getA();
                X1Arg d = ((X1xorq) i).getB();
                for( X1Var v:p.getLiveAfters().get(n)) {
                    if(!v.equals((X1Var) d)) {
                        map.addEdge(d, (X1Var)v );
                    }
                }
            } else if (i instanceof X1movzbq) {
                //s is assumed to be al or another direct bytereg reference,
                //not a variable
                //X1Arg s = ((X1movzbq) i).getA();
                X1Arg d = ((X1movzbq) i).getB();
                for( X1Var v:p.getLiveAfters().get(n)) {
                    //next line is just to patch if it isn't var
                    if(d instanceof X1Int) continue;
                    //if(s instanceof X1Int) s = new X1Var("_");
                    
                    //first argument is always al for now so you don't need to check a
                    if(!v.equals((X1Arg) d)) {
                        map.addEdge(d, (X1Var)v );
                    }
                }
            } else if(i instanceof X1cmpq) {
                //the values of the arguments are not being changed with cmpq,
                //so nothing needs to be done
            } else if(i instanceof X1If) {
                //recursively add to the adjacency map
                X1Var cond = (X1Var) ((X1If) i).getCond();
                
                X1Program ifs = new X1Program(new ArrayList<>(),
                        ((X1If) i).getIfs(),
                        new X1Int(-1));
                ifs.setLiveAfters(((X1If) i).getIfLivAfs());
                X1Program elses = new X1Program(new ArrayList<>(),
                        ((X1If) i).getElses(),
                        new X1Int(-1));
                elses.setLiveAfters(((X1If) i).getElseLivAfs());
                
                
                
                X1Program ifsWithInterference= buildInterference(ifs);
                X1Program elsesWithInterference = buildInterference(elses);
                
                /*
                for each variable in the maps of the returned programs,
                add their mapped values into the corresponding places
                
                //also link the condition variable with all the liveafers
                */
                Map <X1Arg,Set <X1Arg>> ifsMap = ifsWithInterference.getAdjMap().getActualMap();
                Map <X1Arg,Set <X1Arg>> elsesMap = elsesWithInterference.getAdjMap().getActualMap();
                
                //iterate over both maps
                
                for(Map.Entry<X1Arg,Set <X1Arg>> cur: ifsMap.entrySet()) {
                    for(X1Arg cur2:cur.getValue()) {
                        map.addEdge(cur.getKey(), cur2);
                    }
                }
                
                for(Map.Entry<X1Arg,Set <X1Arg>> cur: elsesMap.entrySet()) {
                    for(X1Arg cur2:cur.getValue()) {
                        map.addEdge(cur.getKey(), cur2);
                    }
                }
                
            }
        }
        
        //because the live-after list is no longer needed after interference
        //graph is built, it should throw null to the constructor for that
        
        //next part was for testing
        map.print();
        return new X1Program(p.getVarList(), p.getInstrList(), p.getRetArg(), map);
        
    }
    
    /**
     * returns color graph and amount of colors
     * @param vars
     * @param adjMap
     * @return 
     */
    public static Pair< Integer, Map <X1Arg, Integer> > colorGraph (List <X1Arg> vars, AdjacencyMap adjMap) {
        /*
        pseudocode alg:
        W ← vertices(G)
        while W not ∅ do
            pick a node u from W with the highest saturation,
                breaking ties randomly
            find the lowest color c that is not in {color[v] : v ∈ adjacent(u)}
            color[u] ← c
            W ← W − {u}
        */
        //because of stack memory, "colors" can be infinite,
        //so it's better to think of saturation as
        //the thing which has the most ways it cannot be colored
        
        //initialize map with -1
        
        
        //List <X1Arg> varsAndRegs = adjMap.
        Map <X1Arg, Integer> colorMap = new HashMap<>();
        Map <X1Arg, Integer> saturationMap = new HashMap<>();
        Set <X1Arg> uncolored = adjMap.getVarsAndRegs();
        int colorNumber = 0;
        
        for(X1Arg v:uncolored) {
            colorMap.put(v, -1);
        }
        for(X1Arg v:uncolored) {
            saturationMap.put(v, 0);
        }
        while(uncolored.size() > 0) {
            //get a list of highest saturation vertices
            //do this by looking at the saturation map's entryset
            List <X1Arg> highSatVertices = new ArrayList();
            //first just get the highest saturation value, then get the highest valued keys
            int highestSat = -999;
            //for(Map.Entry<X1Arg, Integer> cur:saturationMap.entrySet()) {
            for(X1Arg cur:uncolored) {
                if(saturationMap.get(cur) == null) {
                    //System.out.println("no edges are found for " + cur.stringify());
                    continue;
                }
                int curSat= saturationMap.get(cur);
                if(curSat > highestSat && uncolored.contains(cur)) {
                    //System.out.println("found new highest");
                    highestSat = curSat;
                }/*
                else {
                    boolean t = uncolored.contains(cur.getKey());
                    System.out.println(uncolored.contains(cur));
                }*/
            }
            
            for(Map.Entry<X1Arg, Integer> cur:saturationMap.entrySet()) {
                if(cur.getValue() == highestSat && uncolored.contains(cur.getKey())) highSatVertices.add(cur.getKey());
            }
            
            //current node being colored
            X1Arg colorMe;
            //if there's multiple pick one at random
            //simple impl: get random number from 0 to highsatvertices.length -1
            //if there's only one element get that one and proceed
            if(highSatVertices.size() > 1) {
                Random rand = new Random();
                int choice = rand.nextInt(highSatVertices.size());
                colorMe = highSatVertices.get(choice);
            } else if(highSatVertices.size() == 1) colorMe = highSatVertices.get(0);
            else{
                //System.out.println("Error: Did not find any nodes");
                continue;
            }
            
            
            
            
            
            
            //to get lowest color above neighbors, put neighbor's colors in list
            
            Set <X1Arg> neighbors = adjMap.getEdges(colorMe);
            List <Integer> neighborColors = new ArrayList<>();
            for(X1Arg a:neighbors) {
                if(a instanceof X1Var)
                    neighborColors.add(colorMap.get( (X1Var) a));
            }
            //then use Collections.max (if there are no neighbors just color it 0)
            int newColor = 0;
            if(neighborColors.size()>0) {
                newColor = Collections.max(neighborColors)+1;
            }
            //colorNumber = newColor+1;
            
            //put that color in the color map
            colorMap.put(colorMe, newColor);
            //add 1 to the saturation map value for all its neighbors
            for(X1Arg a:neighbors) {
                if(saturationMap.get( (X1Arg) a) == null) 
                    continue;
                int oldSat = saturationMap.get( (X1Arg) a);
                saturationMap.put((X1Arg) a, oldSat+1);
                
            }
            uncolored.remove(colorMe);
            //saturationMap.remove(colorMe);
        }
        
        colorNumber = Collections.max(colorMap.values())+1;
        
        //next part for testing:
//        for(Map.Entry<X1Arg,Integer> c:colorMap.entrySet()) {
//            System.out.println("Var/Reg name and color: "+c.getKey().stringify() 
//                    +", " + c.getValue());
//        } System.out.println( "color number: "+ colorNumber);
        
        return new Pair(colorNumber, colorMap);
    }
    
    
    
    public static boolean verticesAreRemaining(Map <X1Arg, Integer> g) {
        //color map is initialized with -1 so if it finds something not -1
        //it returns true
        for(Map.Entry<X1Arg, Integer> curr:g.entrySet()) {
            if(curr.getValue() < 0) return true;
        }
        return false;
    }
    
    public static Pair<Integer, Map<X1Arg, X0Arg>> regAlloc(X1TypedProgram p) {
        return regAlloc(p.getProg());
    }
    
    //returns a map from X1Vars to X0Args
    //then i make a separate method that takes a map from X1 Vars to X0Args 
    //and uses that to generate the program
    //the function that generates the program will allocate varlist.len*8 space
    //so then it can be truly modular
    /**
     * 
     * @param p
     * @return a pair with amount of stack memory allocated and the map
     * from x1args to x0args
     */
    public static Pair<Integer, Map<X1Arg, X0Arg>> regAlloc(X1Program p) {
        //do uncoverlive, build interference, and then get colored graph,
        //then loop through the map, mapping variables first to the registers,
        //then the stack, based on the color
        //then convert p into X0Program, copying code from original assign
        X1Program p2 = uncoverLive(p);
        p2 = buildInterference(p2);
        //this part should also add all the registers
        List <X1Arg> argList = new ArrayList(p2.getVarList());
        Pair<Integer, Map <X1Arg, Integer>> colorPair = colorGraph(argList, p2.getAdjMap());
        Map <X1Arg, Integer> m = colorPair.getValue();
        int numColors = colorPair.getKey();
        //gets the amount of spaces on the stack that are used (assuming all vars
        //take up 8 bytes for int, if it's less than 0 then set it to 0
        
        //there are extra spaces in case there's a function call,
        // because then the reg values need to get pushed onto the stack
        int stackSpaces = colorPair.getKey() + regNames.length;
        stackSpaces = stackSpaces >= 0 ? stackSpaces : 0;
        Map<X1Arg, X0Arg> allocMap = new HashMap<>();
        
        //used to check index in the registers array
        //int n = 0;
        for(Map.Entry<X1Arg, Integer> curr: m.entrySet()) {
            //n++;
            int color = curr.getValue();
            X0Arg a;
            if(curr.getKey() instanceof X1Var) {
                if(color < regNames.length) {
                    allocMap.put(curr.getKey(), new X0Reg(regNames[color]));
                } else  {
                    //allocMap.put(curr.getKey(), new X0RegWithOffset("rsp", (color- regNames.length)*8));
                    allocMap.put(curr.getKey(), new X0RegWithOffset("rsp", (color)*8));
                }
            } else if(curr.getKey() instanceof X1Reg) {
                String regName = curr.getKey().stringify();
                int regIndex = Arrays.asList(regNames).lastIndexOf(regName);
                allocMap.put(curr.getKey(), new X0RegWithOffset("rsp", (numColors + regIndex)*8));
            }
        }
        
        return new Pair(stackSpaces*8, allocMap);
    }
    
    public static X0TypedProgram assignModular (X1TypedProgram p ,
            Pair<Integer, Map<X1Arg, X0Arg>> allocPair ) {
        return new X0TypedProgram(
                assignModular(p.getProg(), allocPair, p.getType()),
                p.getType());
    }
    
    /**
     * The purpose of this is so i can make it interchangeable with other methods
     * of assigning registers/stack locations to variables
     * @param p
     * @param allocPair
     * @return 
     */
    public static X0Program assignModular (X1Program p ,
            Pair<Integer, Map<X1Arg, X0Arg>> allocPair) {
        return assignModular(p, allocPair, null);
    }
    
    public static X0Program assignModular (X1Program p ,
            Pair<Integer, Map<X1Arg, X0Arg>> allocPair ,
            Class type) {
        
        int stackSpace = allocPair.getKey();
        Map<X1Arg, X0Arg> m = allocPair.getValue();
        List <X1Instr> origInstrs = p.getInstrList();
        //don't need to make a map because u can just
        //truncate the string after the _ and then u got the number
        
        /*
        ((structure):
        sub 8*numVars from rsp
        (translate original instrs except for last return)
        mov "arg" into rax
        add 8*numVars to rsp to clean up the stack
        retq rax
        */
        List <X0Instr> newInstrs = new ArrayList<>();
        newInstrs.add(new X0subq(new X0Int(stackSpace), new X0Reg("rsp") ));
        for(X1Instr cur: origInstrs) {
            if(cur instanceof X1addq) {
                
                newInstrs.add(new X0addq(X1ToX0MapConvert(((X1addq) cur).getA(), m), 
                        X1ToX0MapConvert(((X1addq) cur).getB(), m)));
            } else if(cur instanceof X1callq) {
                
                //move all caller save register vals into stack space if they are in the
                //map
                newInstrs.add(new X0Comment("throwing regs into stack"));
                for(String c :regNames) {
                    
                    X1Arg r = new X1Reg(c);
                    if(m.get(r)!= null){
                        newInstrs.add(new X0movq(new X0Reg(r.stringify()), m.get(r)));
                    }
                   
                }
                 newInstrs.add(new X0Comment("done throwing regs into stack"));
                
                newInstrs.add(new X0callq(((X1callq) cur).getLabel()));
                
                //move the register values back from stack space into the registers
                
                for(String c :regNames) {
                    
                    X1Reg r = new X1Reg(c);
                    if(m.get(r)!= null)
                        newInstrs.add(new X0movq(m.get(r),new X0Reg(r.stringify()) ));
                }
                
            } else if(cur instanceof X1movq) {
                newInstrs.add(new X0movq(X1ToX0MapConvert(((X1movq) cur).getA(), m), 
                        X1ToX0MapConvert(((X1movq) cur).getB(), m)));
            } else if(cur instanceof X1retq) {
                //should first get value to be returned and stick it in rax
                X1Arg rArg = p.getRetArg();
                //if int
                if(rArg instanceof X1Int) {
                    newInstrs.add(new X0movq(new X0Int(((X1Int) rArg).getVal()), new X0Reg("rax")));
                }
                //if var
                else if(rArg instanceof X1Var) {
                    //int index = 8*vars.lastIndexOf(rArg) /*+ 8*numUniqueVars*/;
                    //X0RegWithOffset retReg = (X0RegWithOffset) X1toX0Reg(rArg);
                    newInstrs.add(new X0movq(X1ToX0MapConvert(rArg, m), new X0Reg("rax")));
                    
                }
                if(System.getProperty("os.name").startsWith("Windows")) {
                    newInstrs.add(new X0movq(new X0Reg("rax"), new X0Reg("rcx")));
                }
                else {
                    newInstrs.add(new X0movq(new X0Reg("rax"), new X0Reg("rdi")));
                    //now it has to look at the type list and put the type index in rsi
                    if(type == int.class) {
                        
                    } else if(type == boolean.class) {
                        
                    }
                    //otherwise it looks through the list of types until it finds a match
                }
                //then call printint
                
                
                newInstrs.add(new X0callq("printint"));
                
                //the next instruction is added to clean up the stack
                newInstrs.add(new X0addq(new X0Int(stackSpace),
                        new X0Reg("rsp") ));
                
                //the previous steps should NOT be in print step because that makes debugging more awkward.
                newInstrs.add(new X0retq(X1ToX0MapConvert(((X1retq) cur).getX(), m)));
            } else if(cur instanceof X1negq) {
                newInstrs.add(new X0negq(X1ToX0MapConvert(((X1negq) cur).getX(),m)));
            } else if(cur instanceof X1movzbq) {
                
                //you can assume the first arg is a X1ByteReg
                X1ByteReg first = (X1ByteReg) ((X1movzbq) cur).getA();
                newInstrs.add(new X0movzbq(new X0ByteReg(first.getName()), 
                        X1ToX0MapConvert(((X1movzbq) cur).getB(), m)));
                
            } else if(cur instanceof X1set) {
                //first arg is X1ByteReg
                newInstrs.add(new X0set(((X1set) cur).getCc(), 
                        X1ToX0MapConvert(((X1set) cur).getA(), m)));
            } else if(cur instanceof X1xorq) {
                newInstrs.add(new X0xorq(X1ToX0MapConvert(((X1xorq) cur).getA(), m), 
                        X1ToX0MapConvert(((X1xorq) cur).getB(), m)));
            } else if(cur instanceof X1cmpq) {
                newInstrs.add(new X0cmpq(X1ToX0MapConvert(((X1cmpq) cur).getA(), m), 
                        X1ToX0MapConvert(((X1cmpq) cur).getB(), m)));
            } else if(cur instanceof X1If) {
                //recursively do assignmodular on the if and else branches,
                //add the statements you get from the recursive processing into the program
                
                //first assign the variable used in the comparison
                X1Var cond = (X1Var) ((X1If) cur).getCond();
                X0Arg condNew = X1ToX0MapConvert(cond, m);
                
                X1Program ifs = ((X1If) cur).generateIfProgram();
                X1Program elses = ((X1If) cur).generateElsesProgram();
                X0Program ifsAssigned = assignModular(ifs, allocPair);
                X0Program elsesAssigned = assignModular(elses, allocPair);
                
                //for both of the recursively assigned programs, you have to remove the first
                //instruction (because it will be stack allocation)
                ifsAssigned.getInstrList().remove(0);
                elsesAssigned.getInstrList().remove(0);
                
                //use the generated statement lists to generate an X0If
                
                X0If assignedIf = new X0If(condNew, 
                        ifsAssigned.getInstrList(), 
                        elsesAssigned.getInstrList());
                
                newInstrs.add(assignedIf);
                
            }
        }
        
        //then convert X1Instrs to X0Instrs
        return new X0Program(newInstrs);
        
    }
    
    public static X0TypedProgram lowerConditionals (X0TypedProgram p) {
        return new X0TypedProgram(lowerConditionals(p.getProg()), p.getType());
    }
    
    public static X0Program lowerConditionals (X0Program p) {
        List <X0Instr> instrs = p.getInstrList();
        List <X0Instr> newInstrs  = new ArrayList<>();
        for(X0Instr c:instrs) {
            if(c instanceof X0If) {
                //because i make all conditions in if statements
                //into comparisons between variable and true,
                //i change cond into cmpq
                X0Arg x = ((X0If) c).getCond();
                newInstrs.add(new X0cmpq(x, new X0Int(1)));
                
                X0label thenLabel = new X0label("thenLabel_" + numUniqueVars++);
                X0label endLabel = new X0label("endLabel_" + numUniqueVars++);
                X0Program ifs = ((X0If) c).generateIfProgram();
                X0Program elses = ((X0If) c).generateElsesProgram();
                
                X0Program ifsLowered = lowerConditionals(ifs);
                X0Program elsesLowered = lowerConditionals(elses);
                
                //first jmp to thenlabel if cc
                newInstrs.add(new X0jmpif(conditionCode.e, thenLabel));
                //put elses first, then it makes a jump to endlabel
                
                for(X0Instr i:elsesLowered.getInstrList()) {
                    newInstrs.add(i);
                }
                newInstrs.add(new X0jmp(endLabel));
                newInstrs.add(thenLabel);
                for(X0Instr i:ifsLowered.getInstrList()) {
                    newInstrs.add(i);
                }
                newInstrs.add(endLabel);
                
            } else newInstrs.add(c);
        }
        return new X0Program(newInstrs);
    }
    
    public static X0TypedProgram compileControl(R0Program p) throws Exception {
        R2TypedProgram p1 = R0.R2TypeChecker.R2TypeCheck(p);
        X1TypedProgram p2 = select(flatten(uniquify(p1))); 
        X0TypedProgram x = fix(lowerConditionals( assignModular(p2, regAlloc(p2)) ) );
        //should have return type at the top of the program (print x0)
        return x;
    }
    
    public static X0Program compileRegAlloc(R0Program p) {
        X1Program p1 = select(flatten(uniquify(p)));
        X0Program x =  fix(assignModular(p1, regAlloc(p1)));
        return x;
    }
    
    //next one becomes obsolete but will stil be left in for testing.
    public static X0Program assign(X1Program p) {
        
        List <X1Instr> origInstrs = p.getInstrList();
        List <X1Var> vars = p.getVarList();
        //don't need to make a map because u can just
        //truncate the string after the _ and then u got the number
        
        /*
        ((structure):
        sub 8*numVars from rsp
        (translate original instrs except for last return)
        mov "arg" into rax
        add 8*numVars to rsp to clean up the stack
        retq rax
        */
        List <X0Instr> newInstrs = new ArrayList<>();
        newInstrs.add(new X0subq(new X0Int(numUniqueVars*8), new X0Reg("rsp") ));
        for(X1Instr cur: origInstrs) {
            if(cur instanceof X1addq) {
                
                newInstrs.add(new X0addq(X1toX0(((X1addq) cur).getA()), 
                        X1toX0(((X1addq) cur).getB())));
            } else if(cur instanceof X1callq) {
                newInstrs.add(new X0callq(((X1callq) cur).getLabel()));
            } else if(cur instanceof X1movq) {
                newInstrs.add(new X0movq(X1toX0(((X1movq) cur).getA()), 
                        X1toX0(((X1movq) cur).getB())));
            } else if(cur instanceof X1retq) {
                //should first get value to be returned and stick it in rax
                X1Arg rArg = p.getRetArg();
                //if int
                if(rArg instanceof X1Int) {
                    newInstrs.add(new X0movq(new X0Int(((X1Int) rArg).getVal()), new X0Reg("rax")));
                }
                //if var
                else if(rArg instanceof X1Var) {
                    //int index = 8*vars.lastIndexOf(rArg) /*+ 8*numUniqueVars*/;
                    X0RegWithOffset retReg = (X0RegWithOffset) X1toX0(rArg);
                    newInstrs.add(new X0movq(retReg, new X0Reg("rax")));
                    
                }
                                //because i'm only using windows or linux,
                //this next part assumes that if i'm not on windows,
                // i'm on linux. linux takes rdi as first func call arg,
                //and windows takes rcx
                if(System.getProperty("os.name").startsWith("Windows"))
                newInstrs.add(new X0movq(new X0Reg("rax"), new X0Reg("rcx")));
                else newInstrs.add(new X0movq(new X0Reg("rax"), new X0Reg("rdi")));
                //then call printint
                
                newInstrs.add(new X0callq("printint"));
                
                //the next instruction is added to clean up the stack
                newInstrs.add(new X0addq(new X0Int(numUniqueVars*8), new X0Reg("rsp") ));
                
                //the previous steps should NOT be in print step because that makes debugging more awkward.
                newInstrs.add(new X0retq(X1toX0(((X1retq) cur).getX())));
            } else if(cur instanceof X1negq) {
                newInstrs.add(new X0negq(X1toX0(((X1negq) cur).getX())));
            } 
        }
        
        //then convert X1Instrs to X0Instrs
        return new X0Program(newInstrs);
    }
    
    
    public static X0Program assignWithRegs(X1Program p) {
        
        int numUsedRegs = 0;
        List <X1Instr> origInstrs = p.getInstrList();
        List <X1Var> vars = p.getVarList();
        //don't need to make a map because u can just
        //truncate the string after the _ and then u got the number
        
        /*
        ((structure):
        sub 8*numVars from rsp
        (translate original instrs except for last return)
        mov "arg" into rax
        add 8*numVars to rsp to clean up the stack
        retq rax
        */
        List <X0Instr> newInstrs = new ArrayList<>();
        newInstrs.add(new X0subq(new X0Int(
                numUniqueVars>regNames.length?(numUniqueVars-regNames.length)*8:0
        ), new X0Reg("rsp") ));
        for(X1Instr cur: origInstrs) {
            if(cur instanceof X1addq) {
                
                newInstrs.add(new X0addq(X1toX0Reg(((X1addq) cur).getA()), 
                        X1toX0Reg(((X1addq) cur).getB())));
            } else if(cur instanceof X1callq) {
                newInstrs.add(new X0callq(((X1callq) cur).getLabel()));
            } else if(cur instanceof X1movq) {
                newInstrs.add(new X0movq(X1toX0Reg(((X1movq) cur).getA()), 
                        X1toX0Reg(((X1movq) cur).getB())));
            } else if(cur instanceof X1retq) {
                //should first get value to be returned and stick it in rax
                X1Arg rArg = p.getRetArg();
                //if int
                if(rArg instanceof X1Int) {
                    newInstrs.add(new X0movq(new X0Int(((X1Int) rArg).getVal()), new X0Reg("rax")));
                }
                //if var
                else if(rArg instanceof X1Var) {
                    //int index = 8*vars.lastIndexOf(rArg) /*+ 8*numUniqueVars*/;
                    //X0RegWithOffset retReg = (X0RegWithOffset) X1toX0Reg(rArg);
                    newInstrs.add(new X0movq(X1toX0Reg(rArg), new X0Reg("rax")));
                    
                }
                if(System.getProperty("os.name").startsWith("Windows"))
                newInstrs.add(new X0movq(new X0Reg("rax"), new X0Reg("rcx")));
                else newInstrs.add(new X0movq(new X0Reg("rax"), new X0Reg("rdi")));
                //then call printint
                
                newInstrs.add(new X0callq("printint"));
                
                //the next instruction is added to clean up the stack
                newInstrs.add(new X0addq(new X0Int(
                        numUniqueVars>regNames.length?(numUniqueVars-regNames.length)*8:0),
                        new X0Reg("rsp") ));
                
                //the previous steps should NOT be in print step because that makes debugging more awkward.
                newInstrs.add(new X0retq(X1toX0(((X1retq) cur).getX())));
            } else if(cur instanceof X1negq) {
                newInstrs.add(new X0negq(X1toX0(((X1negq) cur).getX())));
            } 
        }
        
        //then convert X1Instrs to X0Instrs
        return new X0Program(newInstrs);
    }
    
    public static X0Arg X1toX0(X1Arg a) {
        if(a instanceof X1Int) {
            return new X0Int(((X1Int) a).getVal());
        } else if(a instanceof X1Var) {
            String [] splitName = ((X1Var) a).getName().split("_");
            int len = splitName.length;
            int offset = Integer.valueOf(splitName[len-1]);
            return new X0RegWithOffset("rsp", offset*8);
        } else if(a instanceof X1Reg) {
            //in the previous step (at least in first iteration), 
            //the only register used is rax
            return new X0Reg(((X1Reg) a).getName());
        }
        return null;
    }
    /**
     * 
     * @param a
     * @return 
     */
    public static X0Arg X1toX0Reg(X1Arg a) {
        if(a instanceof X1Int) {
            return new X0Int(((X1Int) a).getVal());
        } else if(a instanceof X1Var) {
            
            String [] splitName = ((X1Var) a).getName().split("_");
            int len = splitName.length;
            int offset = Integer.valueOf(splitName[len-1]);
            if(offset >= regNames.length) {
                return new X0RegWithOffset("rsp", (offset - regNames.length)*8);
            } else {
                return new X0Reg(regNames[offset]);
            }
        } else if(a instanceof X1Reg) {
            //in the previous step (at least in first iteration), 
            //the only register used is rax
            return new X0Reg(((X1Reg) a).getName());
        }
        return null;
    }
    
    public static X0Arg X1ToX0MapConvert(X1Arg a, Map<X1Arg, X0Arg>  m) {
        if (a instanceof X1Var) {
            return m.get(a);
        } else return intRegConvert(a);
    }
    
    /**
     * Converts X1Arg (int or reg) to X0Arg
     * @param x
     * @return 
     */
    public static X0Arg intRegConvert(X1Arg x) {
        if( x instanceof X1Int) return new X0Int(((X1Int) x).getVal());
        else if (x instanceof X1Reg) return new X0Reg(((X1Reg) x).getName());
        else if(x instanceof  X1ByteReg) return new X0ByteReg(((X1ByteReg) x).getName());
        else{System.err.println("Error converting X1 Arg to X0 Arg"); return null;}
    }
    
    public static X0TypedProgram fix (X0TypedProgram p) {
        return new X0TypedProgram(fix(p.getProg()), p.getType());
    }
    
    public static X0Program fix(X0Program p) {
        List <X0Instr> instrs = p.getInstrList();
        List <X0Instr> newInstrs  = new ArrayList<>();
        for(X0Instr c:instrs) {
            //if current instr has 2 args, check that they're valid
            //if not, split them up
            //right now only addq and movq need to be checked
            if(c instanceof X0addq) {
               X0addq m = (X0addq) c;
                if(m.getA() instanceof X0RegWithOffset && m.getB() instanceof X0RegWithOffset) {
                    newInstrs.add(new X0movq(m.getA(), new X0Reg("rax")));
                    newInstrs.add(new X0addq(new X0Reg("rax"),m.getB() ));
                } else newInstrs.add(c);
            } else if(c instanceof X0movq) {
                X0movq m = (X0movq) c;
                if(m.getA() instanceof X0RegWithOffset && m.getB() instanceof X0RegWithOffset
                        && !m.getA().equals(m.getB())) {
                    newInstrs.add(new X0movq(m.getA(), new X0Reg("rax")));
                    newInstrs.add(new X0movq(new X0Reg("rax"),m.getB() ));
                } else if(m.getA().equals(m.getB())) {
                    //if something is moved into itself
                    continue;
                }
                else newInstrs.add(c);
            } else if(c instanceof X0cmpq) {
                //if the 2nd argument is not literal value, just add the instr as is
                //otherwise put the 2nd val into rax and then add new comparison
                if(!(((X0cmpq) c).getB() instanceof X0Int)) {
                    newInstrs.add(c);
                } else {
                    newInstrs.add(new X0movq(((X0cmpq) c).getB(), new X0Reg("rax")));
                    newInstrs.add(new X0cmpq(((X0cmpq) c).getA(), new X0Reg("rax")));
                }
            }
            else newInstrs.add(c);
        }
        return new X0Program(newInstrs);
    }
    
    public static C1TypedProgram uniquifyTypeCheckAndFlatten(R0Program p) throws Exception {
        R0Program uniquified = uniquify(p);
        R2TypedProgram checked = R2TypeChecker.R2TypeCheck(uniquified);
        C0Program flat = flatten(new R0Program(checked.getExp()));
        Class type = checked.getType();
        if(type == null)throw new Exception();
        C1TypedProgram r = new C1TypedProgram(flat, type);
        return r;
    }
    
    /**
     * first compiler version
     * @param p
     * @return 
     */
    public static X0Program compile1(R0Program p) {
        return fix(assign(select(flatten(uniquify(p)))));
    }
    
    /**
     * naive register allocation
     * @param p
     * @return 
     */
    public static X0Program compile2(R0Program p) {
        return fix(assignWithRegs(select(flatten(uniquify(p)))));
    }
    
    public static String printX0(X0TypedProgram p) {
        return printX0(p.getProg(), p.getType());
    }
    
    public static String printX0(X0Program p) {
        return printX0(p, null);
    }
    /*
    all that has to be changed in this function to make it use the types
    is add the global variables for int and bool type and then add an extra
    one to check for the return type of the program
    when the garbage collection part is done, the types will be in a list
    so it will iterate through the list to add the types
    you have to know what the index of the return type is in the type list so
    you can add it 
    */
    public static String printX0(X0Program p, Class c){
        
        String prog = "";
        //first going to try to compile for cygwin
        
        
        //set up externs (readint, printint)
        
        prog += ".extern printint\n";
        prog += ".extern readint\n";
        prog += ".global main\n.text\n";
        
        //set up stack fram and give extra space
       // prog += "push %rbp\n";
        //prog += "mov %rsp, %rbp\n";
        //prog += "subq $0x20, %rsp\n";
        prog += "#end initialization\n";
        
        prog+="main:\n";
        int n = 0;
        for(X0Instr i:p.getInstrList()) {
            prog+=n+++": ";
            if(i instanceof X0addq) {
                X0addq i2 = (X0addq) i;
                prog += "addq " + printX0Arg(i2.getA()) 
                        +"," + printX0Arg(i2.getB());
            } else if(i instanceof X0movq) {
                X0movq i2 = (X0movq) i;
                prog += "movq " + printX0Arg(i2.getA()) 
                        +"," + printX0Arg(i2.getB());
            } else if(i instanceof X0movzbq) {
                X0movzbq i2 = (X0movzbq) i;
                prog += "movzbq " + printX0Arg(i2.getA()) 
                        +"," + printX0Arg(i2.getB());
            } else if(i instanceof X0negq) {
                X0negq i2 = (X0negq) i;
                prog += "negq " + printX0Arg(i2.getX());
            } else if(i instanceof X0retq) {
                prog+="retq";
                
            } else if(i instanceof X0callq) {
                X0callq i2 = (X0callq) i;
                prog += "callq " + i2.getLabel();
            } else if(i instanceof X0subq) {
                X0subq i2 = (X0subq) i;
                prog += "subq " + printX0Arg(i2.getA()) 
                        +"," + printX0Arg(i2.getB());
            } else if(i instanceof X0Comment) {
                prog +="#"+ ((X0Comment) i).getText();
            } else if(i instanceof X0xorq) {
                X0xorq i2 = (X0xorq) i;
                prog += "xorq " + printX0Arg(i2.getA()) 
                        +"," + printX0Arg(i2.getB());
            } else if (i instanceof X0cmpq) {
                X0cmpq i2 = (X0cmpq) i;
                prog += "cmpq " + printX0Arg(i2.getA()) 
                        +"," + printX0Arg(i2.getB());
            } else if(i instanceof X0set) {
                X0set i2 = (X0set) i;
                prog += "set" + i2.getCc().name()
                        +" " + printX0Arg(i2.getA());
            } else if(i instanceof X0label) {
                prog += ((X0label) i).getName()+":";
            } else if(i instanceof X0movq) {
                X0movq i2 = (X0movq) i;
                prog += "movq " + printX0Arg(i2.getA()) 
                        +"," + printX0Arg(i2.getB());
            } else if(i instanceof X0jmp) {
                X0jmp i2 = (X0jmp) i;
                prog += "jmp " + i2.getL().getName();
            } else if(i instanceof X0jmpif) {
                X0jmpif i2 = (X0jmpif) i;
                prog += "j"+i2.getCc().name()+" " + i2.getL().getName();
            } else prog+= i.getClass();
            prog+="\n";
        }
        
        return prog;
    }
    static public String printX0Arg(X0Arg z) {
        if(z instanceof X0Int) {
            return "$"+String.valueOf(((X0Int) z).getVal());
        } else if (z instanceof X0Reg) {
            return "%"+ ((X0Reg) z).getName();
        } else if (z instanceof X0RegWithOffset) {
            
            String offset = String.valueOf(((X0RegWithOffset) z).getOffset());
            
            return offset + "(%"+((X0RegWithOffset) z).getName()
                     +")";
        } else if(z instanceof X0ByteReg) {
            return "%" + ((X0ByteReg) z).getName();
        }
        return null;
    }
}
