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
import X1.AdjacencyMap;
import static X1.ArgConversion.C0ToX1Arg;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javafx.util.Pair;

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
        /*put here instead of assign: mov arg into rax,
        then  put rax into rdi,
        then call printint,
        then at X1retq (no arguments)*/
        //or if you keep it as retq(ret) then treat retq like a move to rax
        instrs.add( new X1retq(ret));
        return new X1Program(vars, instrs, ret);
    }
    public static X1Program uncoverLive(X1Program p) {
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
            
            if( n == instrs.size()-1) {
                livBefs.set(n, R_k);
                continue;
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
    public static X1Program buildInterference(X1Program p) {
        
        X1Program newProg;
        AdjacencyMap map = new AdjacencyMap();
        List <X1Reg> registers = new ArrayList<>();
        for(String r:regNames) registers.add(new X1Reg(r));
        
        //for xinstr's corresponding liveafter
        int n = -1;
        for(X1Instr i:p.getInstrList()) {
            n++;
            if(i instanceof X1addq) {
                X1Arg s = ((X1addq) i).getA();
                X1Arg d = ((X1addq) i).getB();
                for( X1Var v:p.getLiveAfters().get(n)) {
                    if(v != (X1Var) d) {
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
                    
                    if(v != (X1Var) s && v != (X1Var) d) {
                        map.addEdge(d, (X1Var)v );
                    }
                }
            } else if(i instanceof X1negq) {
                X1Arg d = ((X1negq) i).getX();
                if(d instanceof X1Int) continue;
                for( X1Var v:p.getLiveAfters().get(n)) {
                    if(v != (X1Var) d) {
                        map.addEdge(d, (X1Var)v );
                    }
                }
                
            } else if(i instanceof X1callq) {
                X1Arg x = ((X1retq) i).getX();
                if(x instanceof X1Var) {
                    //will implement after i implement the rest of it
                    for( X1Var v:p.getLiveAfters().get(n)) {
                        for(X1Reg r:registers) map.addEdge(r, v);
                    }
                    
                }
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
            }
        }
        
        //because the live-after list is no longer needed after interference
        //graph is built, it should throw null to the constructor for that
        
        
        return new X1Program(p.getVarList(), p.getInstrList(), p.getRetArg(), map);
        
    }
    
    /**
     * returns color graph and amount of colors
     * @param vars
     * @param adjMap
     * @return 
     */
    public static Pair< Integer, Map <X1Arg, Integer> > colorGraph (List <X1Var> vars, AdjacencyMap adjMap) {
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
        Map <X1Arg, Integer> colorMap = new HashMap<>();
        Map <X1Arg, Integer> saturationMap = new HashMap<>();
        int colorNumber = 0;
        
        for(X1Var v:vars) {
            colorMap.put(v, -1);
        }
        for(X1Var v:vars) {
            saturationMap.put(v, 0);
        }
        while(verticesAreRemaining(colorMap)) {
            //get a list of highest saturation vertices
            //do this by looking at the saturation map's entryset
            List <X1Arg> highSatVertices = new ArrayList();
            //first just get the highest saturation value, then get the highest valued keys
            int highestSat = -1;
            for(Map.Entry<X1Arg, Integer> cur:saturationMap.entrySet()) {
                if(cur.getValue() > highestSat) highestSat = cur.getValue();
            }
            
            for(Map.Entry<X1Arg, Integer> cur:saturationMap.entrySet()) {
                if(cur.getValue() == highestSat) highSatVertices.add(cur.getKey());
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
            } else colorMe = highSatVertices.get(0);
            
            
            
            
            
            
            //to get lowest color above neighbors, put neighbor's colors in list
            
            Set <X1Arg> neighbors = adjMap.getEdges(colorMe);
            List <Integer> neighborColors = new ArrayList<>();
            for(X1Arg a:neighbors) {
                if(a instanceof X1Var)
                    neighborColors.add(colorMap.get( (X1Var) a));
            }
            //then use Collections.max
            int newColor = Collections.max(neighborColors);
            colorNumber = newColor;
            
            //put that color in the color map
            colorMap.put(colorMe, newColor+1);
            //add 1 to the saturation map value for all its neighbors
            for(X1Arg a:neighbors) {
                int oldSat = saturationMap.get( (X1Var) a);
                saturationMap.put((X1Var) a, oldSat+1);
                
            }
        }
        
        return new Pair(colorNumber, colorMap);
    }
    
    
    
    public static boolean verticesAreRemaining(Map <X1Arg, Integer> g) {
        //color map is initialized with -1 so if it finds something not -1
        //it returns true
        for(Map.Entry<X1Arg, Integer> curr:g.entrySet()) {
            if(curr.getValue() >= 0) return true;
        }
        return false;
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
        Pair<Integer, Map <X1Arg, Integer>> colorPair = colorGraph(p2.getVarList(), p2.getAdjMap());
        Map <X1Arg, Integer> m = colorPair.getValue();
        //gets the amount of spaces on the stack that are used (assuming all vars
        //take up 8 bytes for int, if it's less than 0 then set it to 0
        int stackSpaces = colorPair.getKey() + 1 - regNames.length;
        stackSpaces = stackSpaces >= 0 ? stackSpaces : 0;
        Map<X1Arg, X0Arg> allocMap = new HashMap<>();
        
        //used to check index in the registers array
        int n = 0;
        for(Map.Entry<X1Arg, Integer> curr: m.entrySet()) {
            n++;
            X0Arg a;
            
            if(n <= regNames.length) {
                allocMap.put(curr.getKey(), new X0Reg(regNames[n-1]));
            } else 
                allocMap.put(curr.getKey(), new X0RegWithOffset("rsp", (n- regNames.length)*8));
        }
        
        return new Pair(stackSpaces*8, allocMap);
    }
    
    /**
     * The purpose of this is so i can make it interchangeable with other methods
     * of assigning registers/stack locations to variables
     * @param p
     * @param allocPair
     * @return 
     */
    public static X0Program assignModular (X1Program p ,
            Pair<Integer, Map<X1Arg, X0Arg>> allocPair ) {
        
        int stackSpace = allocPair.getKey();
        Map<X1Arg, X0Arg> m = allocPair.getValue();
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
        newInstrs.add(new X0subq(new X0Int(stackSpace), new X0Reg("rsp") ));
        for(X1Instr cur: origInstrs) {
            if(cur instanceof X1addq) {
                
                newInstrs.add(new X0addq(X1ToX0MapConvert(((X1addq) cur).getA(), m), 
                        X1toX0Reg(((X1addq) cur).getB())));
            } else if(cur instanceof X1callq) {
                newInstrs.add(new X0callq(((X1callq) cur).getLabel()));
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
                if(System.getProperty("os.name").startsWith("Windows"))
                newInstrs.add(new X0movq(new X0Reg("rax"), new X0Reg("rcx")));
                else newInstrs.add(new X0movq(new X0Reg("rax"), new X0Reg("rdi")));
                //then call printint
                
                newInstrs.add(new X0callq("printint"));
                
                //the next instruction is added to clean up the stack
                newInstrs.add(new X0addq(new X0Int(stackSpace),
                        new X0Reg("rsp") ));
                
                //the previous steps should NOT be in print step because that makes debugging more awkward.
                newInstrs.add(new X0retq(X1ToX0MapConvert(((X1retq) cur).getX(), m)));
            } else if(cur instanceof X1negq) {
                newInstrs.add(new X0negq(X1ToX0MapConvert(((X1negq) cur).getX(),m)));
            } 
        }
        
        //then convert X1Instrs to X0Instrs
        return new X0Program(newInstrs);
        
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
        else{System.err.println("Error converting X1 Arg to X0 Arg"); return null;}
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
