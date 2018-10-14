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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ProcessBuilder.Redirect;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.exec.CommandLine;
/**
 *
 * @author david
 */
public class testDirectComp {

    public static void runPrintV1(X0Program x) throws IOException {
        //X0Program x = compile(p);
        Process proc;
        File compiledFile;
        File executable;

        String curDir = System.getProperty("user.dir");
        String testDir = curDir + "/src/tests";
        System.out.println(curDir);

        //if i'm on windows (assumes my specific file configuration which is bad)
        if (System.getProperty("os.name").startsWith("Windows")) {
            compiledFile = new File("C:\\Users\\david\\Documents\\cygwin\\temp.s");
            String cygHome = "/cygdrive/c/users/david/Documents/cygwin";
            executable = new File("C:\\Users\\david\\Documents\\cygwin\\tempExec.exe");
            PrintWriter pr = new PrintWriter(compiledFile);
            pr.write(printX0(x));
            pr.close();

            //print program x to a temporary file which is put in the "cygwin" directory, and deleted after
            //compilation
            proc = Runtime.getRuntime().exec(new String[]{"C:\\cygwin64\\bin\\bash.exe", "-c",
                //next line holds all the commands
                "cd " + cygHome + ";"
                + "gcc " + cygHome + "/temp.s runtime.o -o tempExec;"
                + "./tempExec"},
                     new String[]{"PATH=/cygdrive/c/cygwin64/bin"});
        } //if it's not windows then it's linux because those are the only operating systems
        else {
            compiledFile = new File("src/tests/temp.s");
            //String cygHome = "/cygdrive/c/users/david/Documents/cygwin";
            executable = new File("src/tests/tempExec");
            PrintWriter pr = new PrintWriter(compiledFile);
            pr.write(printX0(x));
            pr.close();

            //print program x to a temporary file which is put in the "cygwin" directory, and deleted after
            //compilation
            proc = Runtime.getRuntime().exec(new String[]{"bash", "-c",
                //next line holds all the commands
                "gcc -c runtime.c&&"
                + "gcc temp.s runtime.o -o tempExec"
        //+ "&& time ./tempExec"
            },
                     null,
                    new File(curDir + "/src/tests"));
        }
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

        BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

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

        //NOW try running the generated executable in a newly opened terminal
        //first create a shell script that just runs the executable
        File execute = new File(testDir + "/exec.sh");

        PrintWriter pr2 = new PrintWriter(execute);
        pr2.write("touch finish");
        pr2.close();

        System.out.print(executable.setExecutable(true));

        Process pr = new ProcessBuilder("gnome-terminal", "-e", testDir + "/exec.sh").start();
//        Process pr = Runtime.getRuntime().exec(new String[]
//            {"gnome-terminal", "-e", testDir+"/exec.sh"});

// Process pr =new ProcessBuilder("gnome-terminal", "-e", 
//                  "./progrm").directory(new File("/directory/for/the/program/to/be/executed/from")).start();
        File finish = new File(testDir + "/finish");
        while (!finish.exists()) {
        }
        finish.delete();
        compiledFile.delete();
        executable.delete();
    }

    public static void runPrintV2(X0Program x) throws IOException {
        Process proc;
        File compiledFile = null;
        File executable = null;
        File executor = null;
        try {
            //if i'm on windows (assumes my specific file configuration which is bad)
            if (System.getProperty("os.name").startsWith("Windows")) {
                compiledFile = new File("C:\\Users\\david\\Documents\\cygwin\\temp.s");
                String cygHome = "/cygdrive/c/users/david/Documents/cygwin";
                executable = new File("C:\\Users\\david\\Documents\\cygwin\\tempExec.exe");
                PrintWriter pr = new PrintWriter(compiledFile);
                pr.write(printX0(x));
                pr.close();

                //print program x to a temporary file which is put in the "cygwin" directory, and deleted after
                //compilation
                proc = Runtime.getRuntime().exec(new String[]{"C:\\cygwin64\\bin\\bash.exe", "-c",
                    //next line holds all the commands
                    "cd " + cygHome + ";"
                    + "gcc " + cygHome + "/temp.s runtime.o -o tempExec;"
                    + "./tempExec"},
                         new String[]{"PATH=/cygdrive/c/cygwin64/bin"});
            } //if it's not windows then it's linux because those are the only operating systems
            else {
                compiledFile = new File("src/tests/temp.s");
                //String cygHome = "/cygdrive/c/users/david/Documents/cygwin";
                executable = new File("src/tests/tempExec");
                PrintWriter pr = new PrintWriter(compiledFile);
                pr.write(printX0(x));
                pr.close();

                //print program x to a temporary file which is put in the "cygwin" directory, and deleted after
                //compilation
                String curDir = System.getProperty("user.dir");
                System.out.println(curDir);
                String testDir = curDir + "/src/tests";

//                proc = Runtime.getRuntime().exec(new String[]{"bash", "-i",
//                    //next line holds all the commands
//                    "executor.sh"
//                    },
//                         null,
//                        new File(curDir + "/src/tests"));

                //to have an interactive process, you have to 
                //make a separate sh file with the commands
                
                executor = new File(testDir+"/executor.sh");
                System.out.println(executor.getPath());
                String command = "echo \"assembling\n\"&&"
                                + "gcc -c runtime.c&&"
                                + "gcc temp.s runtime.o -o tempExec"
                                + "&& echo \"running\n\""
                                + "&& ./tempExec;\n"
                        //+ "exec </dev/tty "
//                        + "|| { echo \"ERROR: Unable to connect stdin to /dev/tty\" >&2; exit 1;"
                        ;
                pr = new PrintWriter(executor);
                pr.write(command);
                pr.close();
                executor.setExecutable(true);
                
                
                
                ProcessBuilder pb = new ProcessBuilder()
                        .directory(new File(testDir))
                        .inheritIO()
                        .command("bash", "-c"
                        ,"sh executor.sh");

                proc = pb.start();

            }
            Thread out = new Thread(new ReadThread(proc));
            Thread in = new Thread(new WriteThread(proc));
            Thread err = new Thread(new ErrorThread(proc));

            
            out.start();

            err.start();
            in.start();
            
            //wait for the IO to be done before deleting the exec and temp compiled file
            out.join();
            in.join();
            err.join();
            //in.join();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        compiledFile.delete();
        //executable.delete();
    }

    

    public static void main(String[] args) throws IOException, InterruptedException {

        //2^12 gives blank
        int n = 10;
        //System.out.println(printX0(compile2(Utils.powerOf2(n))));
        //runPrintV2(compile2(Utils.powerOf2(n)));
        Thread.sleep(1);
        runPrintV2(compile2(Utils.powerOf2(n)));
        runPrintV2(compile2(tests.StaticTestList.testList.get(3)));
    }

    // a bad way of thread management
    private static boolean endProcess = false;
    
    //java should just have something that does this for you IMO
    //apachecommonsexec seems not much less ugly to use
    private static class ReadThread implements Runnable {

        Process p;

        public ReadThread(Process p) {
            this.p = p;
        }

        @Override
        public void run() {
            //endProcess = false;
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

            //System.out.println("Here is the standard output of the command:\n");
            String s = null;
            try {
                //while ((s = stdInput.readLine()) != null) {
                while((s = stdInput.readLine()) != null) {
                    System.out.println(s);
                    //yes this is hacky but it is making fair assumption
                    if(s.startsWith("enter ur input")) {
                        BufferedWriter bufferedwriter = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
                        Scanner sc = new Scanner(System.in);
                        bufferedwriter.write(s + "\n");
                        bufferedwriter.flush();
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(testDirectComp.class.getName()).log(Level.SEVERE, null, ex);
            }
            //endProcess = true;
        }

    }

    private static class WriteThread implements Runnable {

        Process p;

        public WriteThread(Process p) {
            this.p = p;
        }

        @Override
        public void run() {
            BufferedWriter bufferedwriter = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
            System.out.println("Enter q when done with program");
            //BufferedReader br = new BufferedReader(new FileReader(fileInput));
            Scanner sc = new Scanner(System.in);
            String s = "b";
            //this next part is not user-friendly but it's basically
            //just making the thread terminate if q is entered,
            //because i don't want to do weird concurrency stuff
            //just to test it for myself
            while (s != null&& !s.equals("q")) {
                try {
                    s = sc.nextLine();
                    bufferedwriter.write(s + "\n");
                } catch (IOException ex) {
                    Logger.getLogger(testDirectComp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            try {
                bufferedwriter.flush();
            } catch (IOException ex) {
                Logger.getLogger(testDirectComp.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    bufferedwriter.close();
                } catch (IOException ex) {
                    //Logger.getLogger(testDirectComp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    private static class ErrorThread implements Runnable {

        Process p;

        public ErrorThread(Process p) {
            this.p = p;
        }

        @Override
        public void run() {
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String s = null;
            try {
                while ((s = stdError.readLine()) != null) {
                    System.out.println(s);
                }
            } catch (IOException ex) {
                Logger.getLogger(testDirectComp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
