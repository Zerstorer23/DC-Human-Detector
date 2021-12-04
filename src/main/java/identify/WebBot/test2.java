package identify.WebBot;

import identify.Objects.Page;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Scanner;

import static identify.BayesianNet.Label.initLabelFromRaw;
import static identify.BayesianNet.Label.initLabels;
import static identify.BayesianNet.Label.label_nick;
import static identify.BayesianNet.NewsClassifier.predict_bayesian;
import static identify.Objects.network.modSentList;
import static identify.Preprocessor.*;
import static identify.WebBot.Scroller.Saratoga;
import static identify.WebBot.Scroller.instances;
import static identify.readSetting.getReader;
import static identify.readSetting.read;

public class test2 {

    public static void main(String[] args) throws IOException, InterruptedException {
    Bot_mobile Lexington = new Bot_mobile();
       // Lexington.parseText("http://gall.dcinside.com/mgallery/board/view?id=blhx&no=672827");
        /*
        gl_filename ="wows";
        initLabelFromRaw("data/bnetwork_"+gl_filename+".txt");*/
        writeNameFile("data/bnetwork_"+gl_filename+"_names.txt");

        String URL = "http://m.dcinside.com/view.php?id=kyoani&no=34993&page=";
        Document doc = Jsoup.connect(URL).userAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1")
                .get();

        /*
        String title = doc.select("span[class=tit_view]").first().text();
        //      String nick = doc.select("span[class=info_edit]").first().text().split(" ")[0];
        String nick = doc.select("span[id=block_nick]").first().text();
        Element body = doc.select("div[id=memo_img]").first();
System.out.println(nick);*/
    }

}
