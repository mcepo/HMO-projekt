
package hmo.projekt;

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
        new Algorithm(instance1);

    }
}
