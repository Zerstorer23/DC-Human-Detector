package identify.WebBot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import static identify.BayesianNet.Label.initLabels;
import static identify.BayesianNet.Label.label_nick;
import static identify.Preprocessor.*;
import static identify.WebBot.Bot.mask;
import static identify.readSetting.getPrinter;
import static identify.readSetting.getReader;

public class refinery {

    public static void main(String[] args) throws IOException, InterruptedException {
        ArrayList<String> sent = new ArrayList<>();
        String fileName = "data/bnetwork_blhx.txt";
        try {
            BufferedReader sc = null;
            sc = getReader(fileName);
            String temp;
            while ((temp = sc.readLine()) != null) {
                sent.add(temp.replace("/", "."));
            } sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        PrintWriter pw = getPrinter(fileName);
        for (int i = 0; i < sent.size(); i++) {
            pw.write(sent.get(i)+"\n");
        }
        pw.close();

        System.out.println("name done!");

    }


}
