package identify.WebBot;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static identify.WebBot.Scroller.botName;
import static identify.WebBot.Scroller.lookedID;
import static identify.WebBot.Scroller.yudong;

public abstract class Bot {
    public boolean nameFilled = false;

    public static WebDriver driver;
    static int turn = 0;
    static String prev = "init";
    public abstract void stay() ;

    public abstract void giveAnswer() throws  InterruptedException;

    public abstract void connectTo(String URL);


    public static String getBotName() {
        String nn = botName + " 改";
        return nn;
    }


    public void logIn()   {
        try {
            driver.get("https://dcid.dcinside.com/join/login.php?s_url=http%3A%2F%2Fgall.dcinside.com%2Fmgallery%2Fboard%2Flists%2F%3Fid%3Dblhx");
            System.out.println("로그인 대기중");
            driver.findElement(By.id("id")).sendKeys("hmshood439");
            driver.findElement(By.id("pw")).sendKeys("gally886");
            Thread.sleep(100);
            driver.findElement(By.id("pw")).sendKeys(Keys.RETURN);
            Thread.sleep(1000);
            System.out.println("로그인 성공");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sendKey(String text) throws InterruptedException {
        try {
            System.out.println("Writing reply "+text);
            driver.findElement(By.cssSelector("textarea[id^='memo_']")).sendKeys(text);
            Thread.sleep(500);
            driver.findElement(By.cssSelector("textarea[id^='memo_']")).sendKeys(Keys.RETURN);
            driver.findElement(By.cssSelector("button[class=btn_blue small repley_add]")).click();
        } catch (WebDriverException e) {

        }
    }
    public void writeName() {
        if (yudong) {
            System.out.println("Write name called");
            String nn = getBotName();
            //   driver.findElement(By.cssSelector("input[placeholder=닉네임]")).sendKeys(nn);
            driver.findElement(By.cssSelector("input[id^='name']")).sendKeys(nn);
            driver.findElement(By.cssSelector("input[id^='password']")).sendKeys("fear");
            nameFilled = true;
        }
    }
    public void initFireFox() {
        //System.setProperty("webdriver.chrome.driver","chromedriver.exe");
        driver = null;
        ProfilesIni profile = new ProfilesIni();
        FirefoxProfile myprofile = profile.getProfile("Intelligence");
        myprofile.setPreference("http.response.timeout", 30);
      String  user_agent = "Mozilla/5.0 (iPhone; U; CPU iPhone OS 3_0 like Mac OS X; en-us) AppleWebKit/528.18 (KHTML, like Gecko) Version/4.0 Mobile/7A341 Safari/528.16";
        myprofile.setPreference("general.useragent.override", user_agent);
        //  myprofile.setPreference("dom.max_script_run_time", 30);
        //   myprofile.setPreference("permissions.default.image", 1); //1 default, 3 blocks thirdparty 2 blocks all
        FirefoxOptions dc = new FirefoxOptions();
        dc.setCapability(FirefoxDriver.PROFILE, myprofile);
        driver = new FirefoxDriver(dc);
        //  driver.get("http://www.google.com/");
    }

    public void initBrowser() {
        System.out.println("Initiating Chrome Driver");
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
     //   Map<String, String> mobileEmulation = new HashMap<>();
     //   mobileEmulation.put("deviceName", "Nexus 5");
        ChromeOptions options = new ChromeOptions();
  //      options.setExperimentalOption("mobileEmulation", mobileEmulation);
        driver = new ChromeDriver(options);
//     driver = new ChromeDriver(capabilities);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS); //응답시간 5초설정
        //    driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS); //응답시간 5초설정
    }

    public static String mask(String src, int n, String letter){
        String out;
        if(src.length()>n){
            String front = src.substring(0,src.length()-n);
            for(int i=0;i<n;i++){
                front=front+letter ;
            }
            out = front;
        } else{
            String end = src.substring(1,src.length());
            end = letter +end;
            out = end;

        }

        return out;
    }
}
