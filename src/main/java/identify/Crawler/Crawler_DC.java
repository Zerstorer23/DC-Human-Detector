package identify.Crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.util.ArrayList;

import static identify.BayesianNet.Label.label_nick;
import static identify.Objects.network.modSentList;
import static identify.Preprocessor.*;
import static identify.readSetting.read;

public class Crawler_DC extends Crawler {
    private static ArrayList<String> url_list = new ArrayList<>();
    public static void main(String[] args) {
        read("setting.txt");
        crawler = new Crawler_DC();
        if (cores > 1) crawler = new Crawler_DC_mx();
        url_list.add("/mgallery/board/view/?id=haruhiism&no=3789&page=1");
        crawler.parseInfo();
    }

    @Override
    public void scrollRaw() {
        if (major) {
            mainPage = "http://gall.dcinside.com/board/lists/?id=" + gallID + "&page=";
        } else {
            mainPage = "http://gall.dcinside.com/mgallery/board/lists/?id=" + gallID + "&page=";
        }

        String tempPage;
        for (int p = 1; p <= MAXPAGE; p = p + increment) {
            tempPage = mainPage + p;
            try {
                Document doc = Jsoup.connect(tempPage).get();
                //System.out.println(doc.html());
                Elements posts = doc.select("tbody").first().select("tr[class=ub-content]");
                //System.out.println("Frequency: "+nums.size()+" "+links.size()+" "+recommends.size()+" ");
                System.out.println("=========Printing page " + p + " : " + (int) (((double) p / (double) MAXPAGE) * 100) + "%");
                for (int i = 0; i < posts.size(); i++) {
                    String indexS = posts.get(i).select("td[class=gall_num]").first().text();
                    if (!indexS.equals("-") && !indexS.equals("공지")) {
                        //check writer
                        String writer = posts.get(i).select("td[class=gall_writer ub-writer]").first().attr("data-nick");
                        if (!writer.equals("ㅇㅇ")) {
                            String url = posts.get(i).select("td[class=gall_tit ub-word]").select("a").first().attr("href");
                            //int index = Integer.parseInt(indexS);
                            url_list.add(url);
                         //   System.out.println("Added: "+url +" || "+indexS);
                        }
                    }
                }
            } catch (ConnectException e) {
                e.printStackTrace();
                p = p - increment;
            } catch (SocketException e) {
                e.printStackTrace();
                p = p - increment;
            } catch (Exception e) {
                e.printStackTrace();
                p = p - increment;
            }
        }
    }

    @Override
    public void parseInfo() {
        for (int i = 0; i < url_list.size(); i++) {
            String URL = "http://gall.dcinside.com" + url_list.get(i);
          //  System.out.println("Connect to "+URL);
            try {
                Document doc = Jsoup.connect(URL).get();
                String title = doc.select("span[class=title_subject]").first().text();
                String nick = doc.select("div[class=gall_writer ub-writer]").first().attr("data-nick");

                Element body = doc.select("div[class=writing_view_box]").first();
                String contents = body.text();
                title = removeChars(title);
                contents = removeChars(contents);
                nick=cleanseNick(nick);
                //  System.out.println(contents);
                if (title.length() > 0) {
                    title = nick + "," + title;
                    modSentList.add(title);
                }
                if (contents.length() > 0){
                    contents = nick + "," + contents;
                    modSentList.add(contents);
                }


                if (!label_nick.contains(nick)) {
                    label_nick.add(nick);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
