/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package R0;

/**
 *
 * @author tennisers
 */
public class R0Test {
    private R0Program prog;
    private String expectedVal;

    public R0Test(R0Program prog, String expectedVal) {
        this.prog = prog;
        this.expectedVal = expectedVal;
    }

    public R0Program getProg() {
        return prog;
    }

    public String getExpectedVal() {
        return expectedVal;
    }
    
    
}
