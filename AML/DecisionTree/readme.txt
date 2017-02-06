                        Homework 1 -- Decision Tree

    Respect to the homework requirement, this program implements a decision
tree learning algorithm by Java (jdk1.8.0_112) and tests accuracy performances 
given data sets, attribute selecting heuristics, and post-pruning parameters.

    1. Program Build Procedure
        1.1 Unzip the zipball, DecisionTree.zip
        1.2 Open a shell and Change the path to:
            "./DecsionTree/src/"
        1.3 Compile the sourcde:
            "javac DecisionTree.java"
        
        Please make sure that: 
            a. Your JDK's version is no older than 1.8.0_112;
            b. Your JDK's path is included in your PATH system varaible;
            c. You can also import the DecisionTree as a whole Java project
               in the Eclipse if you're like to work under the JAVA IDE.
        
    2. Progam Test Specification
        2.1. Interative Test Mode
            The program can be invoked from the command line as:
            
                "java DecisionTree <L> <K> <training-set> <validation-set> <test-set> <to-print>"
            
            All algorithm parameters are specified in the homework requirement: 
                L: integer (used in the post-pruning algorithm)
                K: integer (used in the post-pruning algorithm)
                to-print:{yes,no}
            
            For example:
            
                "C:\Users\mark\workspace\DecisionTree\src>java DecisionTree 100000 30 ..\\data_sets1\\training_set.csv ..\\data_sets1\\validation_set.csv ..\\data_sets1\\test_set.csv no"
    
    
        2.2. Batch Test Mode
            The program can also be invoked as:
            
                "java DecisionTree batch <round>"
            
            The <round> argument specifies the times of each compination of algorithm
            parameters to be executed. The algorithm parameters, L, K, attribute 
            selecting heuristic, and test sets, are configured in such value domains:
                L: [50, 100, 200, 400, 1000, 2000, 4000, 8000, 10000, 16000]
                K: [2, 3, 5, 7, 10, 12, 15, 17, 20, 23, 25, 27]
                heur_id: [0, 1] //0: entropy heuristic 
                                //1: impurity heuristic
                data_set: = {1, 0}; //1: ..\\data_sets1\\xxxyyyzzz.csv 
                                    //0: ..\\data_sets2\\xxxyyyzzz.csv
            
            For example:
                
                "java DecisionTree batch 10"
    
    3. Lab Report
        The report is available in the file "report.csv".
    
    Kindly please let me know if any problem on program building and testing.
    Thank you very much!
    Feb. 1st, 2017
