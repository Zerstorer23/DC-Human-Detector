package identify.WebBot;

import identify.Objects.Page;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriverException;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.Stack;

import static identify.BayesianNet.NewsClassifier.init_bayesian;
import static identify.BayesianNet.NewsClassifier.predict_bayesian;
import static identify.Preprocessor.*;
import static identify.Preprocessor.Max_prediction;
import static identify.WebBot.Scroller.*;
import static identify.readSetting.getReader;
import static identify.readSetting.read;

public class test {

    public static void main(String[] args) throws IOException, InterruptedException {

        read("setting.txt");
   //     readBIN();
       // init_bayesian();
        //System.out.println("Enter setting file name");
        Scanner sc = new Scanner(System.in);
        System.out.println("Start programme");
        if (major) {
            mainPage = "http://gall.dcinside.com/board/lists/?id=" + gallID;
        } else {
            mainPage = "http://gall.dcinside.com/mgallery/board/lists/?id=" + gallID;//ani1_new1
        }
        Bot_mobile Lexington = new Bot_mobile();
//        prediction_machine();
        Lexington.initBrowser();
        Lexington.logIn();
        Lexington.connectTo("http://gall.dcinside.com/mgallery/board/view/?id=blhx&no=672018&page=1");
        Lexington.writeName();
        Lexington.sendKey("dfgh");
    }

    public static void readBIN() throws IOException {
        BufferedReader reader = getReader("bin.txt");

        String temp;
        while ((temp = reader.readLine()) != null) {
            Page haruhi = new Page("default", temp);
            haruhi.writer = "default";
            instances.add(haruhi);
        }
    }

    public static void prediction_machine() throws InterruptedException {
        Bot_mobile Lexington = (Bot_mobile) Saratoga;
        while (!instances.empty()) {
            Page haruhi = instances.pop();
            //Check censoring
            //TODO 설정 봇 디텍션
            if (haruhi.title.length() > 0) {
                String input_text = " ";
                input_text = Lexington.parseText(haruhi.link);


                System.out.println("input: " + input_text);
                String[] predicted = predict_bayesian(input_text);
                String out = "이 글을 쓸만한 사람은 " + predicted[0] + " 이라고 예상됩니다.";
                if (Max_prediction > 1) {
                    out = out + " 그 외의 예상: [";
                    for (int i = 1; i < Max_prediction; i++) {
                        out = out + predicted[i] + "/";
                    }
                    out = out + "]";
                }
            }
        }
    }

}
