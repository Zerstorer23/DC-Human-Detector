package identify.WebBot;

import identify.Objects.Page;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import static identify.Preprocessor.gallID;
import static identify.Preprocessor.mainPage;
import static identify.Preprocessor.major;
import static identify.WebBot.Bot.driver;


public class Scroller {
    //MODE
    public static String botName = "사라토가";

    //CRAWL options
    public static int seconds = 5; // 페이지 스크롤간 간격
    public static double delay = 4.25; //댓글사이의 간격 -- 4.5
    public static boolean yudong = true; //유동닉 사용 여부
    public static boolean emergency = true;//새로운 글만
    public static boolean skipSomePosts = true;
    public static double skipRate = 0.5;
    public static int censor = 0;
    public static boolean doubleOonlyMode=false;
    public static boolean webmode=false;
    public static Bot Saratoga = new Bot_mobile();
    public static boolean initFireFox = false;
    public static Stack<Page> instances = new Stack<>();
    public static ArrayList<String> lookedID = new ArrayList<>();

    public static void scrollDC() throws IOException {
        Document doc = Jsoup.connect(mainPage).maxBodySize(0).get();
        //   System.out.println(mainPage);
        //   System.out.println(doc.html());
        Elements posts = doc.select("tbody").first().select("tr[class=ub-content]");
        //System.out.println("Frequency: "+nums.size()+" "+links.size()+" "+recommends.size()+" ");
        int nCount = 0;
        for (int i = 0; i < posts.size(); i++) {
            String indexS =  posts.get(i).select("td[class=gall_num]").first().text();
            if (!indexS.equals("공지") && !indexS.equals("-")) {
                //check writer
                String writer = posts.get(i).select("td[class=gall_writer ub-writer]").first().attr("data-nick");
                String url = posts.get(i).select("td[class=gall_tit ub-word]").select("a").first().attr("href");
                String link = "http://m.dcinside.com/view.php";
                String token[] = url.split("/");
                String actualURL = "http://gall.dcinside.com"+url;
                link = link+token[token.length-1];
                if (emergency) {
                    lookedID.add(link);//EMERGENCY TODO
                    System.out.println(link);
                } else {
                    if (!lookedID.contains(link)) {
                        String title = " ";
                        Page yuki = new Page(title, link); // TODO
                        yuki.writer = writer;
                        yuki.realURL=actualURL;
                        instances.add(yuki);
                        nCount++;
                    }
                }
            }
        }
        if (emergency) emergency = false;
        if(nCount>0)
            System.out.println("[페이지 로드] 축적된 ID: " + lookedID.size() + "  새로운 링크: " + nCount);

        if (lookedID.size() > 100) {
            lookedID.remove(0);
        }
        //      driver.get("http://m.dcinside.com/view.php?id=blhx&no=672791&page=1");
        //      System.out.println(driver.getCurrentUrl());
    }

    public static String extractCommentView(String link) {
        String[] token = link.split("/");
        if (major) {
            token[2] = "comment_view";
        } else {
            token[3] = "comment_view"; //Minor gall
        }
        String empty = "";
        for (int x = 1; x < token.length; x++) { //0 fo major?
            empty = empty + "/" + token[x];
        }
        return empty;
    }


    public static void ps(String a) {
        System.out.println(a);
    }
}

