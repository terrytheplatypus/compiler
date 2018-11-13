/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package C0;

/**
 *
 * @author tennisers
 */
public class C2GlobalValue implements C0Expression{
    private String name;

    public C2GlobalValue(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    
}
