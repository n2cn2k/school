import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LogisiticRegression extends NBLR {
    //hyperparamters
    double alpha = 0.065;
    double lamda = 0.002;
    double cycles = 120;
    
    double convergence_limit = 0.00001;
    
    Counter counter;
    Reporter reporter;
    
    class Counter {
        Map<String, Integer> dict;
        int feature_size, example_size;
        double examples[][];
        double categories[];
        double weights[];
    };

    class Reporter {
        public final int size;
        double[] docs;
        double[][] sums;
        double accuracy; 
        
        public Reporter (int size) {
            this.size = size;
            this.docs = new double[size];
            this.sums = new double[size][2]; //0 -- correct, 1 -- wrong
        }
    }
    
    private double sigmod(double x) {
        return 1.0 / (1.0 + Math.exp(-1.0 * x));
    }
    
    private int loadData(String dataSetPath, String stopWordPath) {
        this.counter = new Counter();
        try {
            //1. Load Stop Words Pad
            Set<String> stopWords = null;
            if (stopWordPath != null) {
                String fileContent = new String (Files.readAllBytes(Paths.get(stopWordPath)), Charset.defaultCharset());
                stopWords = new HashSet<String>(Arrays.asList(fileContent.split("\\W+")));
            }
            //2. Build Dictionary and Count Examples -- Scan the Data for the 1st time            
            counter.dict = new HashMap<String, Integer>();
            counter.feature_size = counter.example_size = 0;
            
            for (int cateId = 0; cateId < this.catePaths.length; cateId ++) {
                String dataFilePath = dataSetPath + this.catePaths[cateId];
                for (Path path : Files.newDirectoryStream(Paths.get(dataFilePath), "*.txt")) {
                    
                    String fileContent = new String (Files.readAllBytes(Paths.get(path.toString())), Charset.defaultCharset());
                    for (String word : fileContent.split("\\W+")) {
                        if (stopWords != null && stopWords.contains(word)) continue;
                        //word = stemming(word);
                        if (!counter.dict.containsKey(word)) counter.dict.put(word, counter.feature_size ++);
                    }

                    counter.example_size ++;
                }
            }
            //3. Build Examples Matrix and Category Vector -- Scan the Data for the 2nd Time            
            counter.weights = new double[counter.feature_size];
            counter.categories = new double[counter.feature_size];
            counter.examples = new double[counter.example_size][counter.feature_size];
            
            int example_num = 0;
            for (int cateId = 0; cateId < this.catePaths.length; cateId ++) {
                String dataFilePath = dataSetPath + this.catePaths[cateId];
                for (Path path : Files.newDirectoryStream(Paths.get(dataFilePath), "*.txt")) {
                    
                    String fileContent = new String (Files.readAllBytes(Paths.get(path.toString())), Charset.defaultCharset());
                    for (String word : fileContent.split("\\W+")) {
                        if (stopWords != null && stopWords.contains(word)) continue;
                        //word = stemming(word);
                        int feature_num = counter.dict.get(word);
                        counter.examples[example_num][feature_num] += 1.0;
                    }
                    
                    counter.categories[example_num ++] = (double) cateId;
                }
            }
        }
        catch (IOException | DirectoryIteratorException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }
    
    protected int learn(String dataSetPath, String stopWordPath) {
        //1. Initialize Counter
        loadData(dataSetPath, stopWordPath);
        //2. Setup Default Weights Vector
        for (int i = 0; i < counter.feature_size; i ++) counter.weights[i] = 1.0;
        //3. Iterate and Refine the Weights
        double error[] = new double[counter.example_size];
        for (int i = 0; i < cycles; i ++) {
            //3.1 Calculate Error[]: the Error between the Real Class and Estimated Class
            for (int j = 0; j < counter.example_size; j ++) {
                double dot_product = 0.0;
                for (int k = 0; k < counter.feature_size; k ++)
                    dot_product += counter.examples[j][k] * counter.weights[k];
                double hypo_func = sigmod(dot_product);
                error[j] = counter.categories[j] - hypo_func;
            }
            //3.2 Update Weights
            double old_weights[] = counter.weights.clone();
            for (int j = 0; j < counter.example_size; j ++)
                for (int k = 0; k < counter.feature_size; k ++)
                    counter.weights[k] = counter.weights[k] + alpha * (error[j] * counter.examples[j][k] - lamda * counter.weights[k]);
            //3.3 Check Convergence
            double sum_of_sqr_delta = 0.0, sum_of_sqr_weights = 0.0;
            for (int k = 0; k < counter.feature_size; k ++) {
                double delta = old_weights[k] - counter.weights[k];
                sum_of_sqr_delta += delta * delta;
                sum_of_sqr_weights += counter.weights[k] * counter.weights[k];
            }
            double iteration_interval = sum_of_sqr_delta / sum_of_sqr_weights;
            if (iteration_interval < convergence_limit) {
                System.out.printf("iteration: %d, convergence: %f, alpha: %f, lamda: %f\n", i,  sum_of_sqr_delta / sum_of_sqr_weights, alpha, lamda);
                break;
            }
        }
        return 0;
    }
    
    
    protected int inference(String dataSetPath) {
        this.reporter = new Reporter(this.catePaths.length);
        
        try {
            double[] example = new double[counter.feature_size];
            for (int cateId = 0; cateId < this.catePaths.length; cateId ++) {
                String dataFilePath = dataSetPath + this.catePaths[cateId];
                for (Path path : Files.newDirectoryStream(Paths.get(dataFilePath), "*.txt")) {

                    Arrays.fill(example, 0.0);
                    String fileContent = new String (Files.readAllBytes(Paths.get(path.toString())), Charset.defaultCharset());
                    for (String word : fileContent.split("\\W+")) {
                        //word = stemming(word);
                        if (!counter.dict.containsKey(word)) continue;
                        int feature_num = counter.dict.get(word);
                        example[feature_num] += 1.0;
                    }
                    double dot_product = 0.0;
                    for (int i = 0; i < counter.feature_size; i ++)  dot_product += example[i] * counter.weights[i];
                    //int estimated_cate = sigmod(dot_product) < 0.5 ? 0 : 1; 
                    int estimated_cate = dot_product < 0.0 ? 0 : 1;
                    
                    int resultId = estimated_cate == cateId ? 0 : 1;//correct, incorrect
                    reporter.sums[cateId][resultId] += 1.0;
                    reporter.docs[cateId] += 1.0;
                }
            }
            
            double total = 0.0;
            for (int i = 0; i < reporter.size; i ++) {
                System.out.printf("class %d: correct: %.0f wrong: %.0f\n", i, reporter.sums[i][0], reporter.sums[i][1]);
                reporter.accuracy += reporter.sums[i][0];
                total += reporter.docs[i];
                reporter.sums[i][0] /= reporter.docs[i] * 0.01;
                System.out.printf("%s accuracy: %.2f %%100\n", this.catePaths[i].substring(1, this.catePaths[i].length() -1), reporter.sums[i][0]);
                reporter.sums[i][1] /= reporter.docs[i] * 0.01;
            }
            reporter.accuracy /= total * 0.01;
            System.out.printf("total accuracy: %.2f %%100\n", reporter.accuracy);
        }
        catch (IOException | DirectoryIteratorException e) {
            System.out.println(e.getMessage());
        }
        
        return 0;
    }
    
    public int Tuning() {
        double alpha_range[] = {0.05, 0.1};
        double lamda_range[] = {0.001, 0.01};
        double max_cycles = 200;
        double best_accuracy = 0.0;
        
        for (alpha = alpha_range[0]; alpha <= alpha_range[1]; alpha += alpha_range[0]/10.0) {
            for (lamda = lamda_range[0]; lamda <= lamda_range[1]; lamda += lamda_range[0]) {
                cycles = max_cycles;
                learn(trainDS, stopWD);
                inference(testDS);
                if (reporter.accuracy > best_accuracy) {
                    best_accuracy = reporter.accuracy;
                    System.out.printf("alpha: %f, lamda: %f, accuracy: %f\n", alpha, lamda, best_accuracy);
                }
            }
        }
        return 0;
    }
    
    public int Test (String[] args) {
        if (args.length > 2) {
            try {
                this.alpha = Double.parseDouble(args[2]);
                this.lamda = Double.parseDouble(args[3]);
                this.cycles = Integer.parseInt(args[4]);
            }
            catch (Exception e) {
                System.out.println("Usage: java NBLR lr <if-use-stop-words> <alpha> <lamda> <cycles>");
                System.out.println("<if-use-stop-words>: {yes, no}\n");
                System.out.println("For example: java NBLR lr yes 0.065 0.002 100\n");
                return -1;
            }
        }
        System.out.printf("alpha: %f, lamda: %f, cycles: %f\n", alpha, lamda, cycles);
        long start = System.nanoTime();
        learn(trainDS, args[1].toLowerCase().equals("yes") ? stopWD : null);
        long stop = System.nanoTime();
        System.out.printf("Program Running Time: %,d ns.\n", stop - start);
        
        inference(testDS);
        
        //Tuning();
        return 0;
    }
}
