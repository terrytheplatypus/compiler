/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import static compilerPasses.PassMethods.*;
import R0.R0Program;
import X0.X0Program;
import java.io.File;
import java.io.PrintWriter;

/**
 *
 * @author david
 */
public class testDirectComp {
    
    public static void runPrint(X0Program p) throws IOException, InterruptedException {
        
        File compiledFile = new File ("C:\\Users\\david\\Documents\\cygwin\\temp.s");
        String cygHome = "/cygdrive/c/users/david/Documents/cygwin";
        File executable = new File ("C:\\Users\\david\\Documents\\cygwin\\tempExec.exe");
        PrintWriter pr = new PrintWriter(compiledFile);
        pr.write(printX0(p));
        pr.close();
        
        //print program x to a temporary file which is put in the "cygwin" directory, and deleted after
        //compilation
        
        Process proc=  Runtime.getRuntime().exec(new String[]{"C:\\cygwin64\\bin\\bash.exe", "-c", 
                                                        //next line holds all the commands
                                                        "cd "+ cygHome+";"+
                                                        "gcc "+cygHome+"/temp.s runtime.o -o tempExec;"
                                                        //+ "./tempExec;"
                                                        + "time ./tempExec"}
                                                        ,new String[]{"PATH=/cygdrive/c/cygwin64/bin"});
        proc.waitFor();
        
        BufferedReader stdInput = new BufferedReader(new 
        InputStreamReader(proc.getInputStream()));

        BufferedReader stdError = new BufferedReader(new 
             InputStreamReader(proc.getErrorStream()));

        
        // read the output from the command
        System.out.println("Here is the standard output of the command:\n");
        String s = null;
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }

        // read any errors from the attempted command
        System.out.println("Here is the standard error of the command (if any):\n");
        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }
        compiledFile.delete();
        executable.delete();
    }
    
    public static void main(String[] args) throws IOException, InterruptedException {
        
        //2^12 gives blank
        
        int n = 4;
        System.out.println(printX0(compile2(Utils.powerOf2(n))));
        runPrint(compile1(Utils.powerOf2(n)));
        runPrint(compile2(Utils.powerOf2(n)));
 
    }
}
