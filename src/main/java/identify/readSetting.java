package identify;
import java.io.*;
import java.nio.charset.StandardCharsets;

import static identify.Preprocessor.*;
import static identify.WebBot.Scroller.*;

public class readSetting {
    public static void read(String fileName) {
        // Name + UserInfo + CVList+
        try {
            BufferedReader sc = null;
            sc = getReader(fileName);
            String temp;
            while ((temp = sc.readLine()) != null) {
                String[] entry = temp.split(",");
                String head = entry[0];
                String data = entry[1];
                if (head.equals("gallID")) {
                    gallID = data;
                } else if (head.equals("major")) {
                    major = Boolean.parseBoolean(data);
                } else if (head.equals("maxPage")) {
                    MAXPAGE = Integer.parseInt(data);
                } else if (head.equals("increment")) {
                    increment = Integer.parseInt(data);
                }else if (head.equals("cores")) {
                    cores = Integer.parseInt(data);
                }else if (head.equals("filename")) {
                    gl_filename = data;
                }
                else if (head.equals("maxPredict")) {
                    Max_prediction = Integer.parseInt(data);
                }
                else if (head.equals("yudong")) {
                    yudong = Boolean.parseBoolean(data);
                }
                else if (head.equals("emergency")) {
                    emergency = Boolean.parseBoolean(data);
                }
                else if (head.equals("skip")) {
                    skipSomePosts = Boolean.parseBoolean(data);
                }else if (head.equals("botName")) {
                    botName = data;
                } else if(head.equals("doubleOonlyMode")){
                    doubleOonlyMode = Boolean.parseBoolean(data);
                }else if (head.equals("censor")) {
                    censor = Integer.parseInt(data);
                }else if (head.equals("atLeast")) {
                    atLeast = Integer.parseInt(data);
                } else if (head.equals("webmode")) {
                    webmode = Boolean.parseBoolean(data);
                } else if (head.equals("initFireFox")) {
                    initFireFox = Boolean.parseBoolean(data);
                }


                System.out.println(head + " : " + data);
            }
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static BufferedReader getReader(String fileName) {
        BufferedReader sc = null;
        try {
            sc = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileName), "UTF8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return sc;
    }
    public static PrintWriter getPrinter(String fileName) {
        PrintWriter pw= null;
        try {
             pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fileName),
                    StandardCharsets.UTF_8), true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return pw;
    }
}
