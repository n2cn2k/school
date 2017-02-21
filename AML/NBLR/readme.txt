                        Homework 2
    Naive Bayes and Logistic Regression for Text Classification

    Respect to the homework requirement, this program implements Naive Bayes
and Logistic Regression algorithm for text classification by Java (jdk1.8.0_112) 
and tests accuracy performances given data sets, stop words dictionary, and 
relevant hyperparameter parameters.

    The lab report is below:
        1. Naive Bayes
        1.1 Feature Engineering
        1.1.1 Featuring in Common
              Spam Class: 114 correct, 16 wrong (130 Documents)
              Ham  Class: 338 correct, 10 wrong (348 Documents)
              Total     : 452 correct, 26 wrong (478 Documents)
              Accuracy: 94.56 %
        
        1.2.1 Featuring With Stop Words Dictionary
              Spam Class: 121 correct,  9 wrong (130 Documents)
              Ham  Class: 335 correct, 13 wrong (348 Documents)
              Total     : 456 correct, 22 wrong (478 Documents)
              Accuracy: 95.40 %

        1.3.1 Featuring With Stop Words Dictionary and Stemming
              Spam Class: 125 correct,  5 wrong (130 Documents)
              Ham  Class: 332 correct, 16 wrong (348 Documents)
              Total     : 456 correct, 21 wrong (478 Documents)
              Accuracy: 95.61 %              
        
        2. Logistic Regression
        2.1 Feature Engineering 
        (Alpha: 0.065, Lamda: 0.002, Iteration Limit: 120)
        2.1.1 Featuring in Common 
              Spam Class: 109 correct, 21 wrong (130 Documents)
              Ham  Class: 333 correct, 15 wrong (348 Documents)
              Total     : 442 correct, 36 wrong (478 Documents)
              Accuracy: 92.47 %
        
        2.1.2 Featuring With Stop Words Dictionary
              Spam Class: 119 correct, 11 wrong (130 Documents)
              Ham  Class: 335 correct, 13 wrong (348 Documents)
              Total     : 454 correct, 24 wrong (478 Documents)
              Accuracy: 94.98 %
              
        2.2 Parameters Tuning
        (Stop Words Dictionary Featured, Iteration Limit: 120)
        2.2.1 Lamda: 0.0005, Alpha: 0.065
              Spam Class: 117 correct, 13 wrong (130 Documents)
              Ham  Class: 327 correct, 21 wrong (348 Documents)
              Total     : 444 correct, 34 wrong (478 Documents)
              Accuracy: 92.89 %
        
        2.2.2 Lamda: 0.001, Alpha: 0.065
              Spam Class: 117 correct, 13 wrong (130 Documents)
              Ham  Class: 332 correct, 16 wrong (348 Documents)
              Total     : 449 correct, 29 wrong (478 Documents)
              Accuracy: 93.93 %
        
        2.2.3 Lamda: 0.004, Alpha: 0.065
              Spam Class: 112 correct, 18 wrong (130 Documents)
              Ham  Class: 338 correct, 10 wrong (348 Documents)
              Total     : 450 correct, 28 wrong (478 Documents)
              Accuracy: 94.14 %
        
        2.2.4 Lamda: 0.009, Alpha: 0.065
              Spam Class: 115 correct, 15 wrong (130 Documents)
              Ham  Class: 332 correct, 16 wrong (348 Documents)
              Total     : 447 correct, 31 wrong (478 Documents)
              Accuracy: 93.51 %

        2.2.5 Lamda: 0.001, Alpha: 0.02
              Spam Class: 112 correct, 18 wrong (130 Documents)
              Ham  Class: 318 correct, 30 wrong (348 Documents)
              Total     : 430 correct, 48 wrong (478 Documents)
              Accuracy: 89.96 %

        2.2.6 Lamda: 0.004, Alpha: 0.02
              Spam Class: 111 correct, 19 wrong (130 Documents)
              Ham  Class: 334 correct, 14 wrong (348 Documents)
              Total     : 445 correct, 33 wrong (478 Documents)
              Accuracy: 93.10 %

        2.2.7 Lamda: 0.001, Alpha: 0.1
              Spam Class: 118 correct, 12 wrong (130 Documents)
              Ham  Class: 333 correct, 15 wrong (348 Documents)
              Total     : 451 correct, 27 wrong (478 Documents)
              Accuracy: 94.35 %
              
        2.2.8 Lamda: 0.004, Alpha: 0.1
              Spam Class: 118 correct, 12 wrong (130 Documents)
              Ham  Class: 328 correct, 20 wrong (348 Documents)
              Total     : 446 correct, 32 wrong (478 Documents)
              Accuracy: 93.31 %

        3. Discussion
        3.1 Stop Word Dictionary
            From the lab report, we observe that the stop word dictionary 
        significantly improve the learning accuracy in either Naive Bayes or
        Logistic Regression.
            Removal of stop words is actually to shrink redundant information
        which is neutral to both ham and spam. Let us assume a set of documents be
        filled with neutral words (99%) and characteristic words (1%) and it will
        be definitely much more difficult for us to classify them than normal documents
        that include fewer neutral words.
            Thus, the less neutral words mean the more characteristic portion of information.
        Eliminating stop words does help to improve learning accuracy.
        
        3.2 Extra Featuring
            We have two more feature selection methods to be tried.
            The one is to stem words of a document and the other is to strip digit numbers.
        3.2.1 Stemming Verbs
            By simply truncating the terminating "ing" and "ed" of verbs, we furtherly generalize
        effective information to be helpful to classify. From the experiment, stemming does
        improve the learning accuracy for Naive Bayes but for Logistic Regression. Underlying
        mechanism is to be discovered by more researches on document examples and stemming 
        algorithm itself.
        
        3.2.2 Strip Digit Numbers
            By assign digit numbers into stop words, we basically ignore all of them. Unfortunately,
        the learning accuracy gets worse for both Naive Bayes and Logistic Regression. The accuracy
        drops more significantly in ham documents than spam documents. This can be explained by that
        digit numbers are actually used more to carry information by ham documents than spam ones.
        So removal of them does harm to feature engineering.
        
        
    Appendix:
        A1. Program Build Procedure
            A1.1 Unzip the zipball, NBLG.zip
            A1.2 Open a shell and Change the path to:
                "./NBLG/src/"
            A1.3 Compile the sourcde:
                "javac NBLG.java"
            
            Please make sure that: 
                a. Your JDK's version is no older than 1.8.0_112;
                b. Your JDK's path is included in your PATH system varaible;
                c. You can also import the NBLG as a whole Java project
                   in the Eclipse if you're like to work under the JAVA IDE.
            
        A2. Progam Test Specification
            A2.1. Naive Bayes
                The program can be invoked from the command line as:
                
                    "java NBLG nb <if-use-stop-words>"
                
                The parameter is specified as the homework requires: 
                    if-use-stop-words: {yes, no}
                
                For example:
                
                    "C:\Users\mark\workspace\NBLG\src>java NBLG nb yes"
        
        
            A2.2. Logistic Regression
                The program can also be invoked as:
                
                    "java NBLG lg <if-use-stop-words> [alpha lamda cycles]"
                
                All Parameters are specified: 
                    if-use-stop-words: {yes, no}
                    alpha: learning rate
                    lamda: quadratic penalty
                    cycles: covergence iteration
                    
                For example:
                    
                    "C:\Users\mark\workspace\NBLG\src>java NBLG lb yes"
                    or 
                    "C:\Users\mark\workspace\NBLG\src>java NBLG lb yes 0.065 0.002 100"
        
    Kindly please let me know if any problem on program building and testing.
    Thank you very much!
                                                                           
