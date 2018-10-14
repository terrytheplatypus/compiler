/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import java.io.OutputStream;
import java.io.PrintWriter;

/**
 *
 * @author tennisers
 */
public class bla {
    public static void main(String[] args) {
        try {
            Process p = Runtime.getRuntime().exec("/bin/bash");
            OutputStream stdin = p.getOutputStream();
            PrintWriter pw = new PrintWriter(stdin);
            pw.println("./tempExec < /dev/tty > /dev/tty");
            pw.close();
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
