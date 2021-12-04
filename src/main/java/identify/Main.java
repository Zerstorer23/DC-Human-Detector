package identify;

import identify.BayesianNet.Instance;
import identify.Crawler.Crawler_DC;
import identify.Crawler.Crawler_DC_mx;

import java.io.IOException;
import java.util.Scanner;

import static identify.BayesianNet.Label.initLabels;
import static identify.BayesianNet.NewsClassifier.*;
import static identify.Preprocessor.*;
import static identify.WebBot.Scroller.Saratoga;
import static identify.WebBot.Scroller.initFireFox;
import static identify.readSetting.read;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        // write your code here
        read("setting.txt");
        init_bayesian();
        System.out.println("Start programme");
        if (major) {
            mainPage = "http://gall.dcinside.com/board/lists/?id=" + gallID;
        } else {
            mainPage = "http://gall.dcinside.com/mgallery/board/lists/?id=" + gallID;//ani1_new1
        }
        if (initFireFox) {
            Saratoga.initFireFox();
        } else {
            Saratoga.initBrowser();
        }
        Saratoga.stay();
    }
}
