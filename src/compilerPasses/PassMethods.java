/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilerPasses;

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
import R0.*;
import static R0.ConciseConstructors.nAdd;
import static R0.ConciseConstructors.nLet;
import static R0.ConciseConstructors.nNeg;
import static R0.ConciseConstructors.nVar;
import X0.*;
import static X1.ArgConversion.C0ToX1Arg;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    
    //all static variables need to be reset when compile is called on a new program,
    //and because uniquify is the first step, it should set numUniqueVars to 0
    public static R0Program uniquify(R0Program p) {
        numUniqueVars = 0;
        return new R0Program(uniquifyRecursive(p.getExp(), new HashMap<>()));
    }
    
    //very bad space-wise, optimization would be to make a static array of maps,
    //and pass the index of the new map in the uniquifyRec call after doing something
    //w/ let expre
    private static R0Expression uniquifyRecursive(R0Expression e, Map<String, String> varNameList) {
        if(e.getChildren()==null) {
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
        
        else{
            return null; 
        }
        return null;
    }
    
    //flatten
    //IIMPORTANT: C0Program is assumed to have no duplicate variable names
    //so it should only be run with a uniquified R0Program
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
            C0Stmt assignRead = new C0Stmt(rv, new C0Read());
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
            stmts.add(new C0Stmt(nv, new C0Neg(fe.getReturnArg())));
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
            stmts.add(new C0Stmt(av, new C0Add(
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
            stmts.add(new C0Stmt(x, fe1.getReturnArg()));
            stmts.addAll(fe2.getStmtList());
            arg = fe2.getReturnArg();
            return new C0Program(vars, stmts, arg);
        }
        return null;
    }
    
    // assumptions made by select and assign pass: rax is only register used in select pass
    public static X1Program select(C0Program p) {
        
        List <X1Var> vars = new ArrayList<>();
        List <X1Instr> instrs = new ArrayList<>();
         
        
        for(C0Stmt s:p.getStmtList()) {
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
        }
        X1Arg ret = C0ToX1Arg(p.getReturnArg());
        instrs.add( new X1retq(ret));
        return new X1Program(vars, instrs, ret);
    }
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
                newInstrs.add(new X0movq(new X0Reg("rax"), new X0Reg("rcx")));
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
            return new X0Reg("rax");
        }
        return null;
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
                if(m.getA() instanceof X0RegWithOffset && m.getB() instanceof X0RegWithOffset) {
                    newInstrs.add(new X0movq(m.getA(), new X0Reg("rax")));
                    newInstrs.add(new X0movq(new X0Reg("rax"),m.getB() ));
                } else newInstrs.add(c);
            } else newInstrs.add(c);
        }
        return new X0Program(newInstrs);
    }
    
    public static X0Program compile(R0Program p) {
        return fix(assign(select(flatten(uniquify(p)))));
    }
    
    public static String printX0(X0Program p){
        
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
        for(X0Instr i:p.getInstrList()) {
            if(i instanceof X0addq) {
                X0addq i2 = (X0addq) i;
                prog += "addq " + printX0Arg(i2.getA()) 
                        +"," + printX0Arg(i2.getB());
            } else if(i instanceof X0movq) {
                X0movq i2 = (X0movq) i;
                prog += "movq " + printX0Arg(i2.getA()) 
                        +"," + printX0Arg(i2.getB());
            } else if(i instanceof X0negq) {
                X0negq i2 = (X0negq) i;
                prog += "negq " + printX0Arg(i2.getX());
            } else if(i instanceof X0retq) {
                //maybe do extra stuff to clean up stack
                
                /*
                X0retq i2 = (X0retq) i;
                //prog += "retq" + printX0Arg(i2.getX());
                prog+= "movq " + printX0Arg(i2.getX()) +", %rax\n";
                prog+="movq (%rax) ,%rcx\n";
                prog+= "callq printint\n";
                prog+="pop %rbp\n#getting rid of the stack frame\n";
*/
                prog+="retq";
                
            } else if(i instanceof X0callq) {
                X0callq i2 = (X0callq) i;
                prog += "callq " + i2.getLabel();
            } else if(i instanceof X0subq) {
                X0subq i2 = (X0subq) i;
                prog += "subq " + printX0Arg(i2.getA()) 
                        +"," + printX0Arg(i2.getB());
            }
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
            
            String ofset = String.valueOf(((X0RegWithOffset) z).getOffset());
            
            return ofset + "(%"+((X0RegWithOffset) z).getName()
                     +")";
        }
        return null;
    }
}
