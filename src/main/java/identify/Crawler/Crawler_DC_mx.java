package identify.Crawler;

import identify.Objects.UrlInstance;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import static identify.BayesianNet.Label.label_nick;
import static identify.Objects.network.*;
import static identify.Preprocessor.*;


public class Crawler_DC_mx extends Crawler {
    public static ArrayList<Queue<String>> CORES = new ArrayList<>();
    public static int[] core_sizes;
    public static HashMap<String,Integer> attempts = new HashMap<>();
    public static HashMap<String,Integer> nick_frequency = new HashMap<>();
    public static ArrayList<UrlInstance> writer_url_list = new ArrayList<>();
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
        for(int i=0;i<cores;i++){
            core_sizes[i] = CORES.get(i).size();
        }
    }

    public void scrollRaw() {
        if (major) {
            mainPage = "http://gall.dcinside.com/board/lists/?id=" + gallID + "&page=";
        } else {
            mainPage = "http://gall.dcinside.com/mgallery/board/lists/?id=" + gallID + "&page=";
        }
        initCores();
        for (int c = 0; c < cores; c++) {
            int finalC = c;
            Thread go = new Thread(() -> {
                while (!CORES.get(finalC).isEmpty()) {
                    String tempPage = CORES.get(finalC).poll();
                    try {
                        Document doc = Jsoup.connect(tempPage).get();
                        //System.out.println(doc.html());
                        Elements posts = doc.select("tbody").first().select("tr[class=ub-content]");
                        //System.out.println("Frequency: "+nums.size()+" "+links.size()+" "+recommends.size()+" ");

                        for (int i = 0; i < posts.size(); i++) {
                            String indexS = posts.get(i).select("td[class=gall_num]").first().text();
                            if (!indexS.equals("-") && !indexS.equals("공지")) {
                                //check writer
                                String writer = posts.get(i).select("td[class=gall_writer ub-writer]").first().attr("data-nick");
                                if (!writer.equals("ㅇㅇ")) {
                                    String url = posts.get(i).select("td[class=gall_tit ub-word]").select("a").first().attr("href");
                                    //int index = Integer.parseInt(indexS);
                                    String[] token = url.split("/");
                                    String mobileURL = "http://m.dcinside.com/view.php"+token[token.length-1];
                                    UrlInstance haruhi = new UrlInstance(writer,mobileURL);
                                    writer_url_list.add(haruhi);
                                    if(nick_frequency.containsKey(writer)){
                                        nick_frequency.replace(writer,nick_frequency.get(writer)+1);
                                    }else{
                                        nick_frequency.put(writer,1);
                                    }
                                }
                            }
                        }
                    } catch (HttpStatusException e){
                        e.printStackTrace();
                        readd(finalC,tempPage);
                    }catch (Exception e) {
                        e.printStackTrace();
                        readd(finalC,tempPage);
                    }
                }
            });
            go.start();
        }
    }
    public void readd(int coreID, String page){
        System.out.println("Status exception: Re-add");
        if(attempts.containsKey(page)){
            int tried = attempts.get(page);
            System.out.println(page + "  Attempts:"+tried);
            if(tried<12){
                CORES.get(coreID).add(page);
                attempts.replace(page,tried+1);
            }else{
                System.out.println("Stop readding");
            }
        }else{
            CORES.get(coreID).add(page);
            attempts.put(page,1);
            System.out.println("[NEW]"+page + "  Attempts:"+1);
        }
        System.out.println("  Attempt SIZE="+attempts.size());

    }
    public static int original_size;
    @Override
    public void parseInfo() {
        original_size = writer_url_list.size();
        for (int c = 0; c < cores; c++) {
            int finalC = c;
            Thread go = new Thread(() -> {
                while (!CORES.get(finalC).isEmpty()) {
                    String URL = CORES.get(finalC).poll();
               //     System.out.println("Connecting to " + URL);
                    try {
                        Document doc = Jsoup.connect(URL).userAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1")
                                .get();
                        //String title = doc.select("span[class=title_subject]").first().text();
                  //      Element body = doc.select("div[class=writing_view_box]").first();
                   //     String nick = doc.select("div[class=gall_writer ub-writer]").first().attr("data-nick");

                        String title = doc.select("span[class=tit_view]").first().text();
                  //      String nick = doc.select("span[class=info_edit]").first().text().split(" ")[0];
                        String nick = doc.select("span[id=block_nick]").first().text();
                        Element body = doc.select("div[id=memo_img]").first();

                        String contents = body.text();

                        title = removeChars(title);
                        contents = removeChars(contents);
                        nick = cleanseNick(nick);
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
                    }catch (UnknownHostException e ){
                        e.printStackTrace();
                        System.out.println("This is not added.");
                    }catch (HttpStatusException e){
                        e.printStackTrace();
                        readd(finalC,URL);
                    }
                    catch (Exception e) {
                        System.out.println(e.getLocalizedMessage());
                        readd(finalC,URL);
                    }
                }
            });
            go.start();
        }
    }

    public static boolean isReadyToBuild() {
        boolean ready = true;
        System.out.println(" ");
        for (int i = 0; i < cores; i++) {
            double total = core_sizes[i];
            int completed = (int) total - CORES.get(i).size();
            System.out.println("[CORE #" + i + "] Completed: " + completed + " | " + (int) ((completed / total) * 100) + "%");
            if (!CORES.get(i).isEmpty()) ready = false;
        }
        System.out.println(" ");
        return ready;
    }
}
