package identify.Crawler;

import identify.BayesianNet.Instance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static identify.BayesianNet.NewsClassifier.lines;
import static identify.readSetting.getPrinter;
import static identify.readSetting.getReader;

public class Refiner_pedoClass {

    public static ArrayList<String> pedonicks = new ArrayList<>();
    public static ArrayList<String> pedokeys = new ArrayList<>();


    public static void main(String[] args) throws IOException, InterruptedException {
        ArrayList<String> sent = new ArrayList<>();
        readPkeys("data/pedo_condition.txt");
        String fileName = "data/bnetwork_blhx.txt";
        String[] ls = lines(fileName);
        HashMap<String, Boolean> sentences = new HashMap<String, Boolean>();
        String sentence = "";
        String prevWriter = "";
        for (int i = 0; i < ls.length; i++) {
            String[] token = ls[i].split(",");//TODO some are parse with new line?
            if (token.length < 2) {
                sentence = token[0];
            } else if (token.length == 2) {
                //Normal Case
                sentence = token[1];
                prevWriter = token[0];
            }
            if (isPedo(prevWriter, sentence)) {
                sentences.put(sentence, true);
            } else {
                sentences.put(sentence, false);
            }
        }


        PrintWriter pw = getPrinter("data/bnetwork_blhxP.txt");
        sentences.forEach((key, value) -> {
                    String label = "F";
                    if (value) label = "P";
                    pw.write(label + "," + key + "\n");
                }

        );

        pw.close();

        System.out.println("name done!");

    }

    public static void readPkeys(String filename) {
        BufferedReader br = getReader(filename);
        try {
            String s;
            boolean nickmode = true;
            while ((s = br.readLine()) != null) {
                if (s.equals("##Keys")) {
                    nickmode = false;
                } else if (nickmode) {
                    System.out.println("닉: " + s);
                    pedonicks.add(s);
                } else {
                    System.out.println("키워드: " + s);
                    pedokeys.add(s);
                }
            }
            br.close();
        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    public static boolean isPedo(String writer, String sentence) {
        if (pedonicks.contains(writer)) return true;
        for (int i = 0; i < pedokeys.size(); i++) {
            if (sentence.contains(pedokeys.get(i))) return true;
        }
        return false;
    }


}
