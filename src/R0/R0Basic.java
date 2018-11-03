/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package R0;

/**
 *
 * @author tennisers
 * This represents either int, bool, vector or void
 * it's distinct from literal, because vector is not literal, but it can
 * be a return type. 
 */
public interface R0Basic extends R0Expression {
    public abstract String stringify () ;
}
