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

public class NaiveBayes extends NBLR {
    Counter counter;
    Reporter reporter;
    
    class Counter {
        public final int size;
        double[] docs;
        Map<String, double[]> words;
        
        public Counter (int size) {
            this.size = size;
            this.docs = new double[size];
            this.words = new HashMap<String, double[]>();
        }
    }
    
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
    
    private int argMax(double[] a) {
        int max = 0;
        for (int i = 0; i < a.length; i ++)
            if (a[i] > a[max])  max = i;
        return max;
    }
    
    protected String stemming(String word) {
        //if (word.matches("[0-9]+") && word.length() > 2) continue;
        if (word.endsWith("ing")) word = word.substring(0, word.length() - 3);
        if (word.endsWith("ed")) word = word.substring(0, word.length() - 2);
        return word;
    }
    
    protected int learn(String dataSetPath, String stopWordPath) {
        this.counter = new Counter(this.catePaths.length);
        
        try {
            Set<String> stopWords = null;
            if (stopWordPath != null) {
                String fileContent = new String (Files.readAllBytes(Paths.get(stopWordPath)), Charset.defaultCharset());
                stopWords = new HashSet<String>(Arrays.asList(fileContent.split("\\W+")));
            }
            
            double[] cateSums = new double[counter.size];
            for (int cateId = 0; cateId < this.catePaths.length; cateId ++) {
                String dataFilePath = dataSetPath + this.catePaths[cateId];
                for (Path path : Files.newDirectoryStream(Paths.get(dataFilePath), "*.txt")) {
                    
                    String fileContent = new String (Files.readAllBytes(Paths.get(path.toString())), Charset.defaultCharset());
                    for (String word : fileContent.split("\\W+")) {
                        if (stopWords != null && stopWords.contains(word)) continue;
                        word = stemming(word);
                        if (!counter.words.containsKey(word)) counter.words.put(word, new double [counter.size]);
                        counter.words.get(word)[cateId] += 1.0;
                        cateSums[cateId] += 1.0;
                    }
                    counter.docs[cateId] += 1.0;
                    
                }
            }
            
            int wordNum = counter.words.size();
            for (double[] sums : counter.words.values())
                for (int i = 0; i < sums.length; i ++) 
                    sums[i] = Math.log((sums[i] + 1.0) / (cateSums[i] + wordNum));
            
            /*System.out.println(counter.size + " " + counter.docs[0] + " " + counter.docs[1] + " " + counter.words.size());
            for (Entry<String, double[]> e : counter.words.entrySet()) {
                System.out.printf("%s: %.2f, %.2f\n", e.getKey(), e.getValue()[0], e.getValue()[1]);
            }*/
            
        }
        catch (IOException | DirectoryIteratorException e) {
            System.out.println(e.getMessage());
        }
        
        return 0;
    }
    
    protected int inference(String dataSetPath) {
        this.reporter = new Reporter(this.catePaths.length);
        
        try {
            double[] cateSums = new double[counter.size];
            for (int cateId = 0; cateId < this.catePaths.length; cateId ++) {
                String dataFilePath = dataSetPath + this.catePaths[cateId];
                for (Path path : Files.newDirectoryStream(Paths.get(dataFilePath), "*.txt")) {

                    Arrays.fill(cateSums, 0.0);
                    String fileContent = new String (Files.readAllBytes(Paths.get(path.toString())), Charset.defaultCharset());
                    for (String word : fileContent.split("\\W+")) {
                        if (!counter.words.containsKey(word)) continue;
                        word = stemming(word);
                        for (int i = 0; i < counter.size; i ++)
                            cateSums[i] += counter.words.get(word)[i];
                    }
                    int resultId = argMax(cateSums) == cateId ? 0 : 1;//correct, incorrect
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
    
    public int Test (String[] args) {
        learn(trainDS, args[1].toLowerCase().equals("yes") ? stopWD : null);
        inference(testDS);
        return 0;
    }
 }