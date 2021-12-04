package identify.WebBot;

import identify.BayesianNet.Instance;
import identify.Objects.Page;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.MoveTargetOutOfBoundsException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import static identify.BayesianNet.Label.labelHash;
import static identify.BayesianNet.NaiveBayesClassifier.confidence;
import static identify.BayesianNet.NewsClassifier.classifier;
import static identify.BayesianNet.NewsClassifier.predict_bayesian;
import static identify.Preprocessor.Max_prediction;
import static identify.Preprocessor.cleanseNick;
import static identify.Preprocessor.removeChars;
import static identify.WebBot.Scroller.*;
import static identify.readSetting.read;

public class Bot_mobile extends Bot {
    int count = 0;

    public static NumberFormat numberFormat = new DecimalFormat("#0.0");

    public static void main(String[] args) {
        read("setting.txt");
        yudong = true;
        Saratoga.initBrowser();
        Saratoga.connectTo("http://gall.dcinside.com/mgallery/board/view/?id=kyoani&no=34892&page=1");
        ((Bot_mobile) Saratoga).writeReply("사라토가");
    }


    public static String getBotName() {
        String nn = botName + " 改";
        return nn;
    }

    public void logIn() {
        try {
            driver.get("http://m.dcinside.com/auth/login?r_url=http://m.dcinside.com/");
            System.out.println("로그인 대기중");
            driver.findElement(By.id("user_id")).sendKeys("hmshood439");
            driver.findElement(By.id("user_pw")).sendKeys("gally886");
            Thread.sleep(500);
            driver.findElement(By.id("user_pw")).sendKeys(Keys.RETURN);
            //  driver.findElement(By.cssSelector("button[type=submit]")).click();
            Thread.sleep(1000);
            System.out.println("로그인 성공");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendKey(String text) throws InterruptedException {
        try {
            System.out.println("Writing reply " + text);
            System.out.println("Finding key...");
            WebElement comment = driver.findElement(By.id("comment_memo"));
            comment.sendKeys(text);
            Thread.sleep(500);
            WebElement element = driver.findElement(By.cssSelector("button[class=btn-comment-write]"));
            WebDriverWait wait = new WebDriverWait(driver, 5);   // wait for 5 seconds
            wait.until(ExpectedConditions.elementToBeClickable(element));
            Actions actions = new Actions(driver);
            try {
                actions.moveToElement(element).perform();
                element.click();
                //   actions.moveToElement(element).click().perform();
            } catch (MoveTargetOutOfBoundsException e) {
                System.out.println(e.getLocalizedMessage());
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("window.scrollBy(0,500)");
                element.click();
            } catch (Exception e) {
                System.out.println(e.getLocalizedMessage());
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("window.scrollBy(0,500)");
                element.click();
            }
            System.out.println("Clicked.");
        } catch (WebDriverException e) {
            e.printStackTrace();
        }
    }

    public void writeName() {
        if (yudong) {
            System.out.println("Write name called");
            String nn = getBotName();
            //   driver.findElement(By.cssSelector("input[placeholder=닉네임]")).sendKeys(nn);
            driver.findElement(By.id("comment_nick")).sendKeys(nn);
            driver.findElement(By.id("comment_pw")).sendKeys("fear");
            nameFilled = true;
        }
    }

    public void initBrowser() {
        System.out.println("Initiating Chrome Driver");
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        Map<String, String> mobileEmulation = new HashMap<>();
        mobileEmulation.put("deviceName", "Nexus 5");
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("mobileEmulation", mobileEmulation);
        driver = new ChromeDriver(options);
//     driver = new ChromeDriver(capabilities);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS); //응답시간 5초설정
        //    driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS); //응답시간 5초설정

    }

    public void stay() {
        boolean stat = true;
        if (!yudong) {
            logIn();
        }
        do {
            try {
                scrollDC();
                giveAnswer();
                Thread.sleep(seconds * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (stat);

    }


    public void giveAnswer() throws InterruptedException {
        while (!instances.empty()) {
            Page haruhi = instances.pop();
            //Check censoring
            boolean skip = false;
            //TODO 설정 봇 디텍션
            if (haruhi.title.length() > 0) {
                if (skipSomePosts) {
                    double rand = Math.random();
                    skip = rand < skipRate; // rand less than 0.33 is skipped
                    if (skip) System.out.println(haruhi.title + ": 스킵됨 " + rand);
                    if (instances.empty()) skip = false;
                }
                if (!skip && doubleOonlyMode) {
                    if (!haruhi.writer.equals("ㅇㅇ")) skip = true;
                }
                if (haruhi.writer.equals(botName)) skip = true;
                String input_text = " ";
                if (!skip) {
                    try {
                        input_text = parseText(haruhi.realURL);
                        connectTo(haruhi.link);
                        //    if(lookedID.contains(driver.getCurrentUrl()))skip = true;
                    } catch (UnhandledAlertException e) {
                        System.out.println("Unhandled Alert. Close");
                        driver.switchTo().alert().accept();
                        connectTo(haruhi.link);
                    } catch (TimeoutException e) {
                        System.out.println("Timeout, Skip this page");
                        driver.quit();
                        if(initFireFox){initFireFox();}
                        else{initBrowser();}
                        if (!yudong) {
                            logIn();
                        }
                        instances = null;
                        instances = new Stack();
                        skip = true; // SKips all the tasks to be done on this webpage
                    } catch (NoSuchWindowException e) {
                        skip = true;
                    } catch (WebDriverException e) {
                        skip = true;
                    } catch (Exception e) {
                        skip = true;
                    }

                }
                if (!skip) {
                    if (!nameFilled) writeName();
                    System.out.println(haruhi.writer + "/ input: " + input_text);
                    haruhi.writer = cleanseNick(haruhi.writer);
                    if (labelHash.containsKey(haruhi.writer) && !haruhi.writer.equals("ㅇㅇ")) {
                        System.out.println("Training... " + haruhi.writer);
                        //train this 1
                        Instance nagato = new Instance();
                        String[] token = input_text.split(" ");
                        nagato.words = token;
                        nagato.label = haruhi.writer;
                        classifier.train_single(nagato);
                    }

                    String[] predicted = predict_bayesian(input_text);
                    if (containArray(predicted, haruhi.writer) || haruhi.writer.equals("ㅇㅇ")) {
                        if (censor > 0) {
                            for (int i = 0; i < predicted.length; i++) {
                                predicted[i] = mask(predicted[i], censor, "*");
                            }
                        }

                        String out = "이 글을 쓸만한 고닉은 [" + predicted[0] + "] 이라고 예상됩니다.";
                        if (confidence > 1) {
                            out = "이 글을 쓸만한 고닉은 " + numberFormat.format(confidence) + "%로 [" + predicted[0] + "] 이라고 예상됩니다.";
                        }
                        if (Max_prediction > 1) {
                            out = out + " 그 외의 예상: [";
                            for (int i = 1; i < Max_prediction; i++) {
                                out = out + predicted[i] + "/";
                            }
                            out = out + "]";
                        }
                        System.out.println(out);
                       // sendKey(out);
                    } else {
                        System.out.println("예상 실패");

                    }
                }
                count++;
//TODO 5000 원래값
                Thread.sleep(2000);
                lookedID.add(haruhi.link);
                System.out.println("id: "+haruhi.link);
            }
        }
    }

    public boolean containArray(String[] pr, String wr) {
        for (int i = 0; i < pr.length; i++) {
            if (pr[i].equals(wr)) return true;
        }
        return false;
    }

    public void connectTo(String URL) throws TimeoutException {
        System.out.println(" ");
        System.out.println("Selenium connected to " + URL);
        nameFilled = false;
        driver.get(URL);  //접속할 사이트
        writeName();
    }

    public void writeReply(String iii) throws NoSuchElementException {
        try {
            sendKey(iii);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String parseText(String url) {
        String ret = " ";
        try {
            Document doc = Jsoup.connect(url).get();
            System.out.println("");
            String title = doc.select("span[class=title_subject]").first().text();
            //  String nick = doc.select("div[class=gall_writer ub-writer]").first().attr("data-nick");
            Element body = doc.select("div[class=writing_view_box]").first();
            String contents = body.text();
            contents = contents + " " + title;
            contents = removeChars(contents);
            System.out.println(contents);
            if (contents.length() > 0) {
                ret = contents;
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.out.println("This is not added.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
}
