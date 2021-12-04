package identify;

import identify.Crawler.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import static identify.BayesianNet.Label.label_nick;
import static identify.Crawler.Crawler_DC_mx.isReadyToBuild;
import static identify.Crawler.Crawler_DC_mx.original_size;
import static identify.Objects.network.modSentList;
import static identify.WebBot.Scroller.webmode;
import static identify.readSetting.getPrinter;
import static identify.readSetting.read;


public class Preprocessor {

    public static String gallID = "typemoon";
    public static String mainPage = "http://gall.dcinside.com/board/lists/?id=" + gallID + "&page=";
    //MODE                          http://gall.dcinside.com/mgallery/board/lists/?id="+gallID+"&page=4
    static String fileToRead = "network-" + gallID + ".txt";
    public static boolean major = true;

    public final static String EOL = "[EOL]";
    //CRAWL
    //15000개
    public static int MAXPAGE = 60000;
    public static int increment = 3;
    public static int atLeast = 5;
    public static ArrayList<String> sentences = new ArrayList<>();
    public static Crawler crawler = new Crawler_DC_mx();
    public static int cores = 1;
    public static int Max_prediction = 2;
    public static String gl_filename = "default";

    public static void main(String[] args) {
        //  gallID = "idolmaster";
        //   mainPage = "http://gall.dcinside.com/board/lists/?id=" + gallID + "&page=";
        //MODE                          http://gall.dcinside.com/mgallery/board/lists/?id="+gallID+"&page=4
        read("setting.txt");
        crawler = new Crawler_DC_mx_album();
        crawler.scrollRaw();
        if (cores > 1) {
            lock();
        }
        System.out.println("Now parsing...");
        crawler.parseInfo();
        if (cores > 1) {
            lock();
        }
        writeFile("data/bnetwork_" + gl_filename + ".txt");
        writeNameFile("data/bnetwork_" + gl_filename + "_names.txt");
    }

    public static void lock() {
        try {
            Thread.sleep(5 * 1000);
            //need to wait
            while (!isReadyToBuild()) {
                System.out.println("[POLL] Not Ready...");
                Thread.sleep(5 * 1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void writeFile(String fileName) {
        // Sentences 출력
        System.out.println("Original " + original_size);
        System.out.println("Found: " + modSentList.size());
        PrintWriter pw = getPrinter(fileName);
        for (int i = 0; i < modSentList.size(); i++) {
            String content = modSentList.get(i) + "\n";
            pw.write(content);

        }
      /*  for (int i = 0; i < entryNetwork.size(); i++) {
            for (int j = 0; j < entryNetwork.get(i).sentences.size(); j++) {
                String content = entryNetwork.get(i).nickname + "," + entryNetwork.get(i).sentences.get(j) + "\n";
                pw.write(content);
            }
        }*/
        pw.close();

        System.out.println("done!");
    }

    public static void writeNameFile(String fileName) {
        // Sentences 출력

        PrintWriter pw = getPrinter(fileName);
        for (int i = 0; i < label_nick.size(); i++) {
            if (label_nick.get(i) != null) {
                String content = cleanseNick(label_nick.get(i)) + "\n";
                pw.write(content);
            }else{
                System.out.println(i+". was null in label_nick.");
            }
        }
        pw.close();

        System.out.println("name done!");
    }

    public static String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";

    public static String removeChars(String title) {
        title = title.replaceAll(match, " ");
        title = title.replace(",", "");
        title = title.replace("/", "");
        title = title.replace("\n", " ");
        title = title.trim().replaceAll(" +", " ");
        return title.toLowerCase();
    }

    public static String cleanseNick(String nick) {
        nick = nick.replace(",", ".");
        nick = nick.replace("/", ".");
        nick = nick.replace("\n", " ");
        nick = nick.trim().replaceAll(" +", " ");
        return nick;
    }

}
