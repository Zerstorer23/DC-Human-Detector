package identify.BayesianNet;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static identify.BayesianNet.Label.initLabels;
import static identify.Preprocessor.gl_filename;
import static identify.Preprocessor.removeChars;
import static identify.readSetting.getReader;
import static identify.readSetting.read;


/**
 * This is the main method that will load the application.
 * 
 * DO NOT MODIFY
 */

public class NewsClassifier {
    public static NaiveBayesClassifier classifier;
  /**
   * Creates a fresh instance of the classifier.
   * 
   * @return a classifier
   */
  public static NaiveBayesClassifier getNewClassifier() {
    NaiveBayesClassifier nbc = new NaiveBayesClassifier();
    return nbc;
  }

  /**
   * identify.Main method reads command-line flags and outputs either the classifications of the test file or
   * uses cross-validation to compute a mean accuracy of the classifier.
   * 
   * @param args
   * @throws IOException
   */
  public static boolean mute = false;
  public static void main(String[] args) throws IOException {
      read("setting.txt");
    //Read labels
   // initLabels("data/bnetwork_"+gl_filename+"_names.txt");
      initLabels("data/bnetwork_pedoLabel.txt");

    // Output classifications on test data
    int mode = 3;//Integer.parseInt(args[0]);
      mute = true;
      String trainingFile ="data/bnetwork_"+gl_filename+"P.txt";
 //     String testFile ="data/bnetwork_"+gl_filename+".txt";

    Instance[] trainingData = createInstances(trainingFile);
    Instance[] testData = trainingData;

    NaiveBayesClassifier nbc = getNewClassifier();
    nbc.train(trainingData);

   // System.out.println("M_V = "+vocabularySize(trainingData));
   
    if(mode==0) 
	{
        nbc.documents_per_label_count(trainingData);
        nbc.print_documents_per_label_count();
	}
    else if(mode==1)
	{
        nbc.words_per_label_count(trainingData);
        nbc.print_words_per_label_count();
	}
    else if(mode == 2){
     nbc.test(testData);
    }
    nbc.write_net("data/bnetwork_"+gl_filename+"P_processed.txt");
    
  }
    public static void train_and_save() throws IOException{
        //Read labels
        System.out.println("Reading "+"bnetwork_"+gl_filename+"_names.txt");
        initLabels("data/bnetwork_"+gl_filename+"_names.txt");

        System.out.println("Reading "+"bnetwork_"+gl_filename+".txt");
        // Output classifications on test data
        String trainingFile ="data/bnetwork_"+gl_filename+".txt";

        Instance[] trainingData = createInstances(trainingFile);

        classifier = getNewClassifier();
        classifier.train(trainingData);
        classifier.write_net("data/bnetwork_"+gl_filename+"_processed.txt");
    }
    public static void init_bayesian() throws IOException{
        //Read labels
        System.out.println("Reading "+"bnetwork_"+gl_filename+"_names.txt");
        initLabels("data/bnetwork_"+gl_filename+"_names.txt");

        System.out.println("Reading "+"bnetwork_"+gl_filename+"_processed.txt");
        // Output classifications on test data
        String trainingFile ="data/bnetwork_"+gl_filename+"_processed.txt";
        classifier = getNewClassifier();
        classifier.read_net(trainingFile);
    }
    public static String[] predict_bayesian(String input){
      Instance  nagato = new Instance();
      input = removeChars(input);
        String[] token = input.split(" ");
        nagato.words = token;
        nagato.label = "UNKWN";

        String[] nick = classifier.classify(nagato.words);
        return nick;
    }

    private static int vocabularySize(Instance[] data) {
      //TODO check vocabsize when you add
        Set<String> all = new HashSet<String>();
        for (int i = 0; i < data.length; i++) {
            for (int k = 0; k < data[i].words.length; k++) {
                all.add(data[i].words[k]);
            }
        }
        return all.size();
  }

  /**
   * Reads the lines of the input file, treats the first token as the label and cleanses the
   * remainder, returning an array of instances.
   * 
   * @param filename
   * @return
   * @throws IOException
   */
  public static Instance[] createInstances(String filename) throws IOException {
    String[] ls = lines(filename);
    Instance[] is = new Instance[ls.length];
    String prevWriter="";
    for (int i = 0; i < ls.length; i++) {
      //  String[] ws = cleanse(ls[i]).split("\\s");
      String[]token = ls[i].split(",");//TODO some are parse with new line?
        if(token.length<2){
            String[] ws = token[0].split("\\s");
            is[i] = new Instance();
            is[i].words = ws;
            is[i].label = prevWriter;
        }else if(token.length == 2){
            //Normal Case
            String[] ws = token[1].split("\\s");
            is[i] = new Instance();
            is[i].words = ws;
            is[i].label = token[0];
            prevWriter = token[0];
        }else if(token.length > 2){
            System.out.println(i+"."+token.length+"||"+ls[i]+" -> "+token[0]);
            String refineName = token[0];
            for(int x = 1;x<token.length-1;x++){
                refineName = refineName+"."+token[x];
            }
            String[] ws = token[token.length-1].split("\\s");
            is[i] = new Instance();
            is[i].words = ws;
            is[i].label = refineName;
            prevWriter = refineName;
            System.out.println(" -> "+refineName);
        }else{
            System.out.println(i+"."+token.length+"||"+ls[i]+" -> "+token[0]);
        }
      //  System.out.println(ls[i]+" -> "+is[i].label);
    }
    return is;
  }

    private static String cleanse(String s) {
        s = s.replace("?", " ");
        s = s.replace(".", " ");
        s = s.replace(",", " ");
        s = s.replace("/", " ");
        s = s.replace("!", " ");
        return s.toLowerCase();
    }

  public static String[] lines(String filename) throws IOException {
    BufferedReader br = getReader(filename);
    String s;
    List<String> data = new ArrayList<String>();
    while ((s = br.readLine()) != null && !s.isEmpty()) {
      data.add(s);
    }
    br.close();
    return data.toArray(new String[data.size()]);
  }


}
