import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;


public class DecisionTree {
    int huer_id;
    String[] attr;
    
    DecisionTree (int huer_id) {
        this.huer_id = huer_id;//0: entropy heuristic, 1: impurity heuristic
    }
    
    class Tuple {
        String[] name;//feature0, feature1, feature2, ..., class
        int[][] data; //data0, data1, ..., dataN
    }
    
    public class Node {
        int id, label;
        Node left, right, parent;
        
        public Node () {
        }
        public Node (Node node) {
            this.id = node.id;
            this.label = node.label;
            this.left = node.left;
            this.right = node.right;
            this.parent = node.parent;
        }
    }
    
    //Learn JAVA StreamIO operation code samples from -- http://stackoverflow.com/questions/???
    public int countLine(String inputFileName){
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(inputFileName))) {
            while (br.readLine() != null) 
                count ++;
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return count;
    }
    
    public Tuple loadData(String inputFileName){
        Tuple t = new Tuple();
        try (BufferedReader br = new BufferedReader(new FileReader(inputFileName))) {
            int data_num = this.countLine(inputFileName) - 1;   //ignore the name row
            t.name = br.readLine().split(",");
            t.data = new int[data_num][t.name.length];
            for (int i = 0; i < data_num; i ++) { 
                String[] line_elems = br.readLine().split(",");
                for (int j = 0; j < t.name.length; j ++) {
                    t.data[i][j] = Integer.parseInt(line_elems[j]);
                }
            }
            
            if (this.attr == null) {
                this.attr = new String[t.name.length];
                for (int i = 0; i < t.name.length; i ++) this.attr[i] = new String(t.name[i]); 
            }
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return t;
    }
    
    public int dumpData(Tuple t) {
        for (int j = 0; j < t.name.length - 1; j ++) {
            System.out.print(t.name[j] + " ");
        }
        System.out.println("  ==> " + t.name[t.name.length - 1]);
        
        for (int i = 0; i < t.data.length; i ++) { 
            for (int j = 0; j < t.data[i].length - 1; j ++) {
                System.out.print(t.data[i][j] + " ");
            }
            System.out.println("  ==> " + t.data[i][t.data[i].length - 1]);
        }
        System.out.println(t.data.length + " : " + (t.data[0].length - 1) + " + " + 1);
        return 0;
    }
    
    public int sumPos(List<Integer> ids, Tuple t) {
        int c = t.data[0].length - 1; //class
        int pos = 0;
        for (int i : ids) pos += t.data[i][c];
        return pos;
    }
    
    public double getDataMetrics(int pos, int size) { 
        double r = 0.0;
        if (pos != 0 && pos != size) {
            double p_pos = (double) pos / size;
            if (huer_id == 0)      //0: entropy heuristic,
                r = (p_pos * Math.log(p_pos) + (1.0 - p_pos) * Math.log(1.0 - p_pos)) / Math.log(2.0) * (-1.0);
            else if (huer_id == 1) //1: impurity heuristic
                r = p_pos * (1.0 - p_pos);
        }
        return r;
    }
    
    public double calcInfoGain(Map<Integer, List<Integer>> map, double e, Tuple t) {
        double p0 = (double) map.get(0).size() / (map.get(0).size() + map.get(1).size());
        double p1 = 1.0 - p0;
        double g = e - p0 * this.getDataMetrics(this.sumPos(map.get(0), t),  map.get(0).size())
                     - p1 * this.getDataMetrics(this.sumPos(map.get(1), t),  map.get(1).size());
        return g;
    }
    
    public Node createTree(Set<Integer> name_ids, List<Integer> data_ids, Tuple t) {
        if (data_ids.isEmpty()) {
            //System.out.println("data are empty.");
            return null;
        }
        
        Node node = new Node();
        node.id = -1;
        int pos_num = this.sumPos(data_ids, t);
        node.label = (pos_num < data_ids.size() - pos_num) ? 0 : 1;
        
        if (pos_num == 0 || pos_num == data_ids.size()) return node; //purity
        if (name_ids.isEmpty()) return node; //impurity
        //if (node.size <= 4) return node; //no over matching
            
        node.id = name_ids.iterator().next();
        double max_gain = 0.0;
        double base = this.getDataMetrics(pos_num, data_ids.size());
        Map<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>( );
        map.put(0, new ArrayList<Integer>()); 
        map.put(1, new ArrayList<Integer>());
        for (int i : name_ids) {
            for (int j : data_ids) map.get(t.data[j][i]).add(j);
            double gain = this.calcInfoGain(map, base, t);
            if (gain > max_gain) {
                node.id = i;
                max_gain = gain;
            }
            map.get(0).clear();
            map.get(1).clear();
        }
        for (int j : data_ids) map.get(t.data[j][node.id]).add(j);

        name_ids.remove(node.id);
        Set<Integer> name_ids_copy = new HashSet<Integer>();
        name_ids_copy.addAll(name_ids);
        node.left = createTree(name_ids, map.get(0), t);
        node.right = createTree(name_ids_copy, map.get(1), t);
        return node;
    }
    
    public int dumpTree(Node node, int depth) {
        if (node != null) {
            if (node.left == null || node.right == null) {
                System.out.println(node.label);
            }
            else {
                System.out.println("");
                for (int i = 0; i < depth; i ++) System.out.print("|  ");
                System.out.print(this.attr[node.id] + " = 0 : ");
                dumpTree(node.left, depth + 1);
                for (int i = 0; i < depth; i ++) System.out.print("|  ");
                System.out.print(this.attr[node.id] + " = 1 : ");
                dumpTree(node.right, depth + 1);
            }
        }
        return 0;
    }
    
    public Node trainData(String inputFileName) {
        Tuple t = this.loadData(inputFileName);
        //this.dumpData(t);
        Set<Integer> name_ids = new HashSet<Integer>();
        for (int i = 0; i < t.data[0].length - 1; i ++) name_ids.add(i);
        List<Integer> data_ids = new ArrayList<Integer>();
        for (int i = 0; i < t.data.length; i ++) data_ids.add(i);

        Node root = createTree(name_ids, data_ids, t);
        return root;
    }
    
    public boolean traverseTree(Node tree, int i, Tuple t) {
        int c = t.data[0].length - 1;//
        while (tree != null) {
            if (tree.id == -1) return tree.label == t.data[i][c];
            tree = t.data[i][tree.id] == 0 ? tree.left : tree.right;
        }
        return false;
    }
    
    
    public double testData(String inputFileName, Node root) {
        Tuple t = this.loadData(inputFileName);
        
        int correct = 0;
        for (int i = 0; i < t.data.length; i ++)
            correct += traverseTree(root, i, t) ? 1 : 0;
        double accuracy = (double) correct / t.data.length;
        //System.out.println("Accurate: " + accuracy);
        return accuracy;
    }
    
    
    public Node copyTree(Node root) {
        Node copy = null;
        if (root != null) {
            copy = new Node(root);
            copy.left = copyTree(root.left);
            copy.right = copyTree(root.right);
        }
        return copy;
    }
    
   public List<Node> findAllNonLeafNodes(Node root) {
        List<Node> result = new ArrayList<Node>();
        if (root == null) return result;
        
        List<Node> queue = new ArrayList<Node>();
        queue.add(root);
        while (!queue.isEmpty()) {
            Node node = queue.remove(0);
            
            if (node != null && node.id != -1) result.add(node);
            
            if (node.left != null) queue.add(node.left);
            if (node.right != null) queue.add(node.right);
        }
        return result;
    }
    
    public Node pruneTree(Node root, String inputFileName, int L, int K) {
        Node best_tree = this.copyTree(root);
        double best_accuracy = 0.0;
        for (int i = 0; i < L; i ++) {
            Node refined_tree = this.copyTree(root);
            Random r0 = new Random(); 
            int M = r0.nextInt(1 + K);
            for (int j = 0; j < M; j ++) {
                List<Node> result = findAllNonLeafNodes(refined_tree);
                if (result.size() > 0) {
                    int N = result.size();
                    Random r1 = new Random(); 
                    int P = r1.nextInt(N);
                    Node node = result.get(P);
                    if (node != null) {
                        //System.out.println("merge node: " + node.id + " " + node.size);
                        node.left = node.right = null;
                        node.id = -1;
                        //this.validateData(inputFileName, refined_tree, node);
                    }
                }
            }
            double refined_accuracy = this.testData(inputFileName, refined_tree);
            if (refined_accuracy > best_accuracy) {
                best_tree = this.copyTree(refined_tree);
                best_accuracy = refined_accuracy;
                //System.out.println("best accuracy: " + best_accuracy);
            }
        }        
        //System.out.println("best accuracy: " + best_accuracy);
        return best_tree;
    }
    
    public static double unitTest(int L, int K, int heur_id, boolean printFlag, String trainFile, String validationFile, String testFile, int round) {
        double best_accuracy = 0.0;
        for (int i = 0; i < round; i ++) {
            DecisionTree dt = new DecisionTree(heur_id);
            Node root = dt.trainData(trainFile);
            double accuracy_validation = dt.testData(validationFile, root);
            double accuracy_test = dt.testData(testFile, root);
            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(2);
            
            Node root2 = dt.pruneTree(root, validationFile, L, K);
            
            double accuracy_validation2 = dt.testData(validationFile, root2);
            double accuracy_test2 = dt.testData(testFile, root2);
            if (accuracy_test2 > best_accuracy) { 
                best_accuracy = accuracy_test2;
                if (printFlag) {
                    dt.dumpTree(root, 0);
                }
                
                System.out.println("Before Pruning -- Validation Accuracy: " + nf.format(accuracy_validation*100) + "% , Test Accuracy: " + nf.format(accuracy_test*100) + "%");
                System.out.println("After Pruning  -- Validation Accuracy: " + nf.format(accuracy_validation2*100) + "% , Test Accuracy: " + nf.format(accuracy_test2*100) + "%");
                
                System.out.println("Accuracy Improvment  -- Validation: " + nf.format((accuracy_validation2 - accuracy_validation) * 100/ accuracy_validation)
                                                      + "% , Test: " + nf.format((accuracy_test2 - accuracy_test) * 100/accuracy_test) + "%");
                Date date = new Date();
                DateFormat df = new SimpleDateFormat("(MM/dd/yyyy HH:mm:ss)");
                System.out.printf("[%s round: %d/%d -- data_set: %d, heur_id: %d, L: %d, K: %d]\n", df.format(date), i, round, trainFile.contains("data_sets1") ? 1 : 2, heur_id, L, K);
            }
            
        }
        return best_accuracy;
    }
    
    public static void batchTest(int round) {
        int L[] = {50, 100, 200, 400, 1000, 2000, 4000, 8000, 10000, 16000};
        int K[] = {2, 3, 5, 7, 10, 12, 15, 17, 20, 23, 25, 27};
        int heur_id[] = {0, 1};
        int data_set[] = {1, 0};
        String filenames_set[][] = {{"..\\data_sets1\\training_set.csv", "..\\data_sets1\\validation_set.csv", "..\\data_sets1\\test_set.csv"},
                                    {"..\\data_sets2\\training_set.csv", "..\\data_sets2\\validation_set.csv", "..\\data_sets2\\test_set.csv"}};
        
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        
        double best_accuracy = 0.0;
        for (int d : data_set)
            for (int h : heur_id) {
                best_accuracy = 0.0;
                for (int l : L)
                    for (int k : K) {
                        double accuracy = unitTest(l, k, h, false, filenames_set[d][0], filenames_set[d][1], filenames_set[d][2], round);
                        if (accuracy > best_accuracy) {
                            best_accuracy = accuracy;
                            Date date = new Date();
                            DateFormat df = new SimpleDateFormat("(MM/dd/yyyy HH:mm:ss)");
                            System.out.printf("{%s Best Accuracy: %s%% -- data_set: %d, heur_id: %d -- L: %d, K: %d (round: %d)}\n", df.format(date), nf.format(best_accuracy*100), d, h, l, k, round);
                        }
                    }
            }
    }
    
    public static void main(String []args){
        if (args.length == 2 && args[0].equals("batch")) {
            batchTest(Integer.parseInt(args[1]));
            return;
        }
        
        if (args.length < 6) {
            System.out.println("Usage: java DecisionTree <L> <K> <training-set> <validation-set> <test-set> <to-print>");
            System.out.println("L: integer (used in the post-pruning algorithm)");
            System.out.println("K: integer (used in the post-pruning algorithm)");
            System.out.println("to-print:{yes,no}\n");
            return;
        }
        
        int heur_id = 0; //0: entropy heuristic,//1: impurity heuristic
        int L, K; 
        boolean printFlag = true;
        String trainFile, validationFile, testFile;
        
        try {
            L = Integer.parseInt(args[0]);
            K = Integer.parseInt(args[1]);
            trainFile = args[2];
            validationFile = args[3];
            testFile = args[4];
            printFlag = (args[5].toLowerCase().equals("yes") || args[5].toLowerCase().equals("true"));
            if (args.length >= 7) heur_id = (Integer.parseInt(args[6]) == 0) ? 0 : 1;
            //.\program <L> <K> <training-set> <validation-set> <test-set> <to-print>
        }
        catch (Exception e) {
            System.out.println("There are some errors when parsing command line.");
            System.out.println("we fall back to default arguements.");
            L = 200;
            K = 10;
            trainFile = "../data_sets1/training_set.csv";
            validationFile = "../data_sets1/validation_set.csv";
            testFile = "../data_sets1/test_set.csv";
            heur_id = 0;
        }
        
        double max_accuracy = unitTest(L, K, heur_id, printFlag, trainFile, validationFile, testFile, 1);
        return;    
    }
}