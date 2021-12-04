package identify.Crawler;

import identify.Objects.UrlInstance;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.Queue;

import static identify.BayesianNet.Label.label_nick;
import static identify.Crawler.Crawler_DC_mx.*;
import static identify.Objects.network.modSentList;
import static identify.Preprocessor.*;


public class Crawler_DC_mx_album_pClassify extends Crawler {
    private void initCores() {
        for (int i = 0; i < cores; i++) {
            Queue<String> lists = new LinkedList<>();
            CORES.add(lists);
        }
        String tempPage;
        for (int p = 1; p <= MAXPAGE; p = p + increment) {
            tempPage = mainPage + p;
            int index = p % cores;
            CORES.get(index).add(tempPage);
        }

        core_sizes = new int[cores];
        for (int i = 0; i < cores; i++) {
            core_sizes[i] = CORES.get(i).size();
        }
    }

    public void scrollRaw() {
        if (major) {
            mainPage = "http://gall.dcinside.com/board/lists/?id=" + gallID + "&list_num=30&board_type=album&page=";
        } else {
            mainPage = "http://gall.dcinside.com/mgallery/board/lists/?id=" + gallID + "&list_num=30&board_type=album&page=";
        }
        initCores();
        for (int c = 0; c < cores; c++) {
            int finalC = c;
            Thread go = new Thread(() -> {
                while (!CORES.get(finalC).isEmpty()) {
                    String tempPage = CORES.get(finalC).poll();
                    try {
                        Document doc = Jsoup.connect(tempPage).get();
                      // System.out.println(doc.html());
                        Elements posts = doc.select("tbody").first().select("tr[class=album_head brd_topblue ub-content]");
                        Elements bodies = doc.select("tbody").first().select("tr[class=album_body ub-content]");
                        //System.out.println("Frequency: "+nums.size()+" "+links.size()+" "+recommends.size()+" ");

                        for (int i = 0; i < posts.size(); i++) {
                       //     String indexS = posts.get(i).select("td[class=gall_subject]").first().attr("data-no");
                       //     if (!indexS.equals("-") && !indexS.equals("공지")) {
                                //check writer
                                String writer = posts.get(i).select("div[class=gall_writer ub-writer]").first().attr("data-nick");
                                if (!writer.equals("ㅇㅇ")) {
                                    String url = posts.get(i).select("td[class=gall_tit ub-word]").select("a").first().attr("href");
                                    //int index = Integer.parseInt(indexS);
                                    String mobileURL = "http://gall.dcinside.com" + url;
                                    UrlInstance haruhi = new UrlInstance(writer, mobileURL);
                                    String title = posts.get(i).select("td[class=gall_tit ub-word]").select("a").first().text();
                                    String content = bodies.get(i).select("div[class=album_contbox]").select("div[class=album_txtbox album_more_content]").first().text();
                                    haruhi.title = title;
                                    haruhi.content = content;
                                //    System.out.println(haruhi.writer);
                              //      System.out.println(haruhi.title);
                               //     System.out.println(haruhi.content);
                                    writer_url_list.add(haruhi);
                                    if (nick_frequency.containsKey(writer)) {
                                        nick_frequency.replace(writer, nick_frequency.get(writer) + 1);
                                    } else {
                                        nick_frequency.put(writer, 1);
                                    }
                                }
                            }
                      //  }
                    } catch (HttpStatusException e) {
                        e.printStackTrace();
                        readd(finalC, tempPage);
                    } catch (Exception e) {
                        e.printStackTrace();
                        readd(finalC, tempPage);
                    }
                }
            });
            go.start();
        }
    }

    public void readd(int coreID, String page) {
        System.out.println("Status exception: Re-add");
        if (attempts.containsKey(page)) {
            int tried = attempts.get(page);
            System.out.println(page + "  Attempts:" + tried);
            if (tried < 12) {
                CORES.get(coreID).add(page);
                attempts.replace(page, tried + 1);
            } else {
                System.out.println("Stop readding");
            }
        } else {
            CORES.get(coreID).add(page);
            attempts.put(page, 1);
            System.out.println("[NEW]" + page + "  Attempts:" + 1);
        }
        System.out.println("  Attempt SIZE=" + attempts.size());

    }

    @Override
    public void parseInfo() {
        for (int i = 0; i < writer_url_list.size(); i++) {
            if (writer_url_list.get(i) != null) {
                String name = writer_url_list.get(i).writer;
                if (nick_frequency.get(name) > atLeast) {
                    String nick = cleanseNick(name);
                    String title = removeChars(writer_url_list.get(i).title);
                    String contents = removeChars(writer_url_list.get(i).content);

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
                }
            } else {
                System.out.println(i + ". is null");
            }
        }
        core_sizes = new int[cores];
        for (int i = 0; i < cores; i++) {
            core_sizes[i] = CORES.get(i).size();
        }
    }

}
