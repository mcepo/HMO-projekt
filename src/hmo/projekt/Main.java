
package hmo.projekt;

import hmo.projekt.GeneticAlgorithm.Algorithm;

/**
 *
 * @author Marko
 * 
 * 
 */

public class Main {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Instance instance1 = new Instance(Local.instanceFilePath);
        Algorithm alg =new Algorithm(instance1);
        alg.solve();

    }
}
