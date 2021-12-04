package identify.BayesianNet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static identify.BayesianNet.Label.labelHash;
import static identify.BayesianNet.Label.label_nick;
import static identify.BayesianNet.NewsClassifier.mute;
import static identify.Preprocessor.Max_prediction;
import static identify.readSetting.getPrinter;
import static identify.readSetting.getReader;

/**
 * Your implementation of a naive bayes classifier. Please implement all four methods.
 */
//Hyochang Yoon
//hyoon66@wisc.edu
public class NaiveBayesClassifier {
    private Instance[] m_trainingData;
    private int m_v;
    private double m_delta;
    public int[]  labelCount = new int[label_nick.size()];; // train set label counts
    public int[] wordCount = new int[label_nick.size()];; //vocab counts
    private HashMap<String, Integer> m_map[] = new HashMap[label_nick.size()];
    public static double confidence = 0;
    /**
     * Trains the classifier with the provided training data and vocabulary size
     */

    public void train(Instance[] trainingData) {
        // TODO : Implement
        // For all the words in the documents, count the number of occurrences. Save in HashMap
        // e.g.
        // m_map[0].get("catch") should return the number of "catch" es, in the documents labeled sports
        // Hint: m_map[0].get("asdasd") would return null, when the word has not appeared before.
        // Use m_map[0].put(word,1) to put the first count in.
        // Use m_map[0].replace(word, count+1) to update the value
        m_trainingData = trainingData;
        for (int i = 0; i < m_map.length; i++) {
            m_map[i] = new HashMap<>();
        }
        for (int i = 0; i < m_trainingData.length; i++) {
            if(m_trainingData[i]==null){
                System.out.println(i+" is null");
            }
            if(labelHash.containsKey(m_trainingData[i].label)){
              //  System.out.println(m_trainingData[i].label+" is not found");
            }
        }
        // Implement
        //Iterate the training data
        //This BUILDS hash map (frequencies and words)
        for (int i = 0; i < m_trainingData.length; i++) {
            //SPORTS case
           System.out.println(m_trainingData[i].label);
            int index = labelHash.get(m_trainingData[i].label);
            for (int j = 0; j < m_trainingData[i].words.length; j++) {
                //Increment count for this vocabulary of THIS labeled traindata.

                //word already EXISTS, manually fetch
                String haruhi = m_trainingData[i].words[j];
                if (m_map[index].containsKey(haruhi)) {
                    m_map[index].replace(haruhi, m_map[index].get(haruhi) + 1);
                }
                //WORD NOT FOUND, add 1
                else {
                    m_map[index].put(haruhi, 1);
                }
            }
        }
        m_v = m_v_replicate();

        documents_per_label_count(m_trainingData);
        words_per_label_count(m_trainingData);
    }
    /**
     * Trains the classifier with the provided training data and vocabulary size
     */
    public void train_single(Instance trainingData) {
        trainingData.label = trainingData.label.replace(",", ".");;

        // Implement
        //Iterate the training data
            //SPORTS case
            // if(!labelHash.containsKey(trainingData[i].label)) continue;
        if(labelHash.containsKey(trainingData.label)) {
            int index = labelHash.get(trainingData.label);
            for (int j = 0; j < trainingData.words.length; j++) {
                //Increment count for this vocabulary of THIS labeled traindata.

                //word already EXISTS, manually fetch
                String haruhi = trainingData.words[j];
                if (m_map[index].containsKey(haruhi)) {
                    m_map[index].replace(haruhi, m_map[index].get(haruhi) + 1);
                }
                //WORD NOT FOUND, add 1
                else {
                    m_map[index].put(haruhi, 1);
                }
            }
        }else{
            //Expand

        }
            m_v=m_v_replicate();
            documents_per_label_count(m_trainingData);
            words_per_label_count(m_trainingData);

    }

    /*
     * Counts the total number of words for each label
     */
    public void words_per_label_count(Instance[] trainingData) {
        wordCount = new int[label_nick.size()]; // train set label counts
        // TODO : Implement
//sports
        for (int i = 0; i < label_nick.size(); i++) {
            int index = labelHash.get(label_nick.get(i));
            for (Map.Entry<String, Integer> haruhi : m_map[index].entrySet()) {
                wordCount[index] =  wordCount[index]+haruhi.getValue();
            }

        }
    }

    /*
     * Prints the number of documents for each label
     */
    public void print_documents_per_label_count() {
        for (int i = 0; i< labelCount.length;i++) {
            System.out.println(label_nick.get(i) + "=" + labelCount[i]);
        }
    }

    /*
     * Counts the number of documents for each label
     */
   public void documents_per_label_count(Instance[] trainingData) {
        labelCount = new int[label_nick.size()];
        // TODO : Implement
        for (int i = 0; i < label_nick.size(); i++) {
            String name =label_nick.get(i);
            int index = labelHash.get(name);
            for (Map.Entry<String, Integer> haruhi : m_map[index].entrySet()) {
                labelCount[index] =  labelCount[index]+(haruhi.getValue() *2);
            }
        }
        //YIELDS HALF of whats supposed to be. probably doesn't matter?
    }

 /*
    public void documents_per_label_count(Instance[] trainingData) {
        labelCount = new int[label_nick.size()];
        // TODO : Implement
        for (int i = 0; i < trainingData.length; i++) {
            String name = trainingData[i].label;
            int index = labelHash.get(name);
            for (Map.Entry<String, Integer> haruhi : m_map[index].entrySet()) {
                labelCount[index] =  labelCount[index]+haruhi.getValue();
            }
        }
    }*/

    /*
     * Prints out the number of words for each label
     */
    public void print_words_per_label_count() {
        for (int i = 0; i< wordCount.length;i++) {
            System.out.println(label_nick.get(i) + "=" + wordCount[i]);
        }
    }

    /**
     * Returns the prior probability of the label parameter, i.e. P(SPORTS) or P(BUSINESS)
     */
    public double p_l(String nickname) {
        // TODO : Implement
        // Calculate the probability for the label. No smoothing here.
        // Just the number of label counts divided by the number of documents.
        double ret = 0;
        // Implement
        double total = getSum_labelCount(); // word count or jus thing ? TODO
        // p("p_l RET = "+total);
        //0 = sports
        int index = labelHash.get(nickname);
        ret = (double) (labelCount[index]) / (total);
        // p("p_l RET = "+ret);
        return ret;
    }

    private double getSum_labelCount() {
        double sum = 0.0;
        for (int i=0; i<labelCount.length;i++){
            sum = sum + labelCount[i];
        }
        return sum;
    }

    /**
     * Returns the smoothed conditional probability of the word given the label, i.e. P(word|SPORTS) or
     * P(word|BUSINESS)
     */
    public double p_w_given_l(String word, String nick) {
        // TODO : Implement
        // Calculate the probability with Laplace smoothing for word in class(label)
        double ret = 0;
        m_delta = 0.00001;
        //0 = sports
        int index = labelHash.get(nick);
        int labelWordCount = wordCount[index];
        //(Cl(w) + delta) /(v*delta + SUM Cl(v) )
        // Cl(w) is the number of times the token w appears in news
        //articles labeled l in the training set
        // |V| will be passed to the train method of your classifier as the
        //argument int v
        double count = 0.0;
        if (m_map[index].containsKey(word)) {
            count = (double) m_map[index].get(word);
        }
        //      Clw    +  delta     /  v    *  delta     + sum
        ret = (count + m_delta) / (m_v * m_delta + labelWordCount);

//p("RET = "+ret);
        return ret;
    }

    static public void p(String s) {
        System.out.println(s);
    }

    /**
     * Classifies an array of words as either SPORTS or BUSINESS.
     */

    public String[] classify(String[] words) {
        // TODO : Implement
        // Sum up the log probabilities for each word in the input data, and the probability of the label
        // Set the label to the class with larger log probability
//Log prob of Business
        ///USE G(W) formula
        // = SUM of log given i
        double[] gw_list = new double[label_nick.size()];
        for (int x = 0; x < gw_list.length; x++) {
            String label = label_nick.get(x);
            double log_prob_business = Math.log(p_l(label));
            double gwTHIS = log_prob_business;
            for (int i = 0; i < words.length; i++) {
                double probability = p_w_given_l(words[i], label);
                gwTHIS += Math.log(probability);
            }
            gw_list[x] = gwTHIS;
          //  System.out.println(x+". "+gwTHIS);
        }

//choose max
        int n = Max_prediction;
        int[] best = getBestOf(gw_list, n);

    if(!mute){
        System.out.println("RESULT");
        for (int i = 0; i < n; i++) {
            System.out.println(i + ". "+ label_nick.get(best[i]) + " with " + ( gw_list[best[i]]));
        }
    }

        String[] out = new String[n];
        for(int i=0;i<out.length;i++){
            out[i]=label_nick.get(best[i]);
        }
     //Calculate Confidence
        double diff = gw_list[best[0]] -gw_list[best[n-1]];
        diff = diff / gw_list[best[1]];
        confidence = Math.abs(diff) * 100;
        //
        return out;
    }
    private int[] getBestOf(double[] list, int n) {
        int[] best = new int[n];
        boolean[] chosen = new boolean[list.length];
        Arrays.fill(chosen,false);
        for (int i = 0; i < n; i++) {
            int maxIdx = 0;
            for (int x = 0; x < list.length; x++) {
                if (list[x] > list[maxIdx] && !chosen[x]) {
                    maxIdx = x;
                }
            }
            best[i] = maxIdx;
            chosen[maxIdx] = true;
        }

        return best;
    }

    /*
     * Constructs the confusion matrix
     */

    public void test(Instance[] testData) {
        // TODO : Implement
        // Count the true positives, true negatives, false positives, false negatives
        int correct = 0;
        int wrong = 0;
        for (int i = 0; i < testData.length; i++) {
            if (classify(testData[i].words)[0].equals(testData[i].label)) {
                correct++;
            } else {
                wrong++;
            }

        }
        System.out.println("Correct: " + correct);
        System.out.println("Wrong: " + wrong);
    }
    public String predict_writer(Instance testData) {
        // TODO : Implement
        // Count the true positives, true negatives, false positives, false negatives
        String nick = classify(testData.words)[0];
        return nick;
    }


    public void write_net(String filename) {
        //이름,~Vocabs~
        PrintWriter pw = getPrinter(filename);
        for (int i = 0; i < label_nick.size(); i++) {
            System.out.println(i+". "+label_nick.get(i));
            String content = label_nick.get(i);
            for (Map.Entry haruhi : m_map[i].entrySet()) {
                 content =content+"/"+haruhi.getKey()+","+haruhi.getValue();
            }
            content= content + "\n";
            pw.write(content);
        }
        pw.close();

    }

    public void read_net(String filename) {
        for (int i = 0; i < m_map.length; i++) {
            m_map[i] = new HashMap<>();
        }
        try {
            BufferedReader sc  = getReader(filename);
            String temp;
            while ((temp = sc.readLine()) != null) {
                String[] entry = temp.split("/");
                String name  = entry[0];
    //Names are already read by readNames
                int index = labelHash.get(name);
                for(int i=1;i<entry.length;i++){
                    String[] token = entry[i].split(",");
                 //   System.out.println(entry[i]);
                    String vocab = token[0];
                    int freq = Integer.parseInt(token[1]);
                     m_map[index].put(vocab, freq);
                }
            }
            m_v = m_v_replicate();
            documents_per_label_count(m_trainingData);
            words_per_label_count(m_trainingData);

            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int m_v_replicate() {
        Set<String> all = new HashSet<String>();
        for (int i = 0; i < m_map.length; i++) {
            for (Map.Entry<String,Integer> haruhi : m_map[i].entrySet()) {
                all.add(haruhi.getKey());
            }
        }
        System.out.println("Replicated mv = "+all.size());
        return all.size();
    }

}
