import java.io.File;

public class NBLR {
    protected final String pathPrefix = (new File(".\\src").exists()) ? ".\\src" : ".\\";
    protected final String trainDS = pathPrefix + ".\\hw2_train\\train\\";
    protected final String testDS  = pathPrefix + ".\\hw2_test\\test\\";
    protected final String stopWD  = pathPrefix + ".\\stopWords.txt";
    protected final String[] catePaths = {"/spam/", "/ham/"};
    
    protected int learn(String dataSetPath, String stopWordPath) { return 0; }
    protected int inference(String dataSetPath) { return 0; }
    public int Test(String[] args) { return 0; }
    
    
    public static void usage() {
        System.out.println("Usage: java NBLR <algo-type> <if-use-stop-words> [hyperparameters]");
        System.out.println("<algo-type>: {nb, lr}");
        System.out.println("<if-use-stop-words>: {yes, no}\n");
        System.out.println("For example: java NBLR nb yes\n");
        return;
    }
    
    public static void main(String []args){
        //System.out.println(args[0] + " " + args[1] + " " + args.length);
        long start = System.nanoTime();
        if (args.length < 2 || 
            !args[0].toLowerCase().equals("nb") && !args[0].toLowerCase().equals("lr") ||
            !args[1].toLowerCase().equals("yes") && !args[1].toLowerCase().equals("no")) {
            usage();
            return;
        }
        
        NBLR solution = args[0].toLowerCase().equals("nb") ? new NaiveBayes() : new LogisiticRegression();
        solution.Test(args);
        
        
        long stop = System.nanoTime();
        System.out.printf("Program Running Time: %,d ns.\n", stop - start);
        
        return;
    }
 }