package identify.WebBot;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriverException;

import java.io.IOException;
import java.util.List;

import static identify.WebBot.Scroller.botName;
import static identify.WebBot.Scroller.yudong;

public class htmlBot {
    private WebClient driver = null;
    public  void stay() {};

    public void giveAnswer() throws  InterruptedException{};

    public void connectTo(String URL) throws IOException {
        System.out.println("Selenium connected to " + URL);
        driver.getPage(URL);  //접속할 사이트
        writeName();
    };
    public void writeName() {
        if (yudong) {
            System.out.println("Write name called");
            String nn = getBotName();
            //   driver.findElement(By.cssSelector("input[placeholder=닉네임]")).sendKeys(nn);

            HtmlPage page = (HtmlPage) driver.getCurrentWindow().getEnclosedPage();
            List<HtmlForm> htmlf = page.getForms(); // 폼들을 가져온다.
            HtmlForm form = htmlf.get(2);
            System.out.println("폼 얻음 : " + form.asXml()); // 입력필드와 버튼이 있는 폼인 2번 인덱스의 폼을 가져온다.

            form.getInputByName("name").setValueAttribute(nn);

            System.out.println("닉네임 설정 : " + nn); // 미리 입력 받아둔 닉네임을 입력한다.

            form.getInputByName("password").setValueAttribute("fear");

            System.out.println("비밀번호 설정 : " + "fear"); // 미리

        }
    }

    public static String getBotName() {
        String nn = botName + " 改";
        return nn;
    }


    public void logIn()   {
        try {
            HtmlPage page= driver.getPage("https://dcid.dcinside.com/join/login.php?s_url=http%3A%2F%2Fgall.dcinside.com%2Fmgallery%2Fboard%2Flists%2F%3Fid%3Dblhx");
            List<HtmlForm> forms = page.getForms();
            System.out.println("로그인 대기중");
            HtmlForm form = page.getFormByName("login");
            System.out.println("폼 얻음 : " + form.asXml()); // 입력필드와 버튼이 있는 폼인 2번 인덱스의 폼을 가져온다.
            form.getInputByName("user_id").setValueAttribute("hmshood439");
            System.out.println("이름 성공");
            form.getInputByName("password").setValueAttribute("gally886");
            System.out.println("비밀번호 성공");
            Thread.sleep(500);
            HtmlElement input = form.getElementsByAttribute("button", "type", "submit").get(0);
            HtmlButton button =(HtmlButton) input;
            button.click();
            Thread.sleep(1000);
            System.out.println("로그인 성공");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void sendKey(String text) throws InterruptedException, IOException {
        try {
            System.out.println("Writing reply "+text);
            HtmlPage page =(HtmlPage) driver.getCurrentWindow().getEnclosedPage();
           // HtmlElement input = page.getElementsByAttribute("button", "type", "submit").get(0);
        //    form.getTextAreaByName("memo").setText(text);
            System.out.println("댓글 내용 설정 : " + text);
            Thread.sleep(500);
            HtmlButton button = page.getHtmlElementById( "re_write" );
            button.click(); // 댓글 작성 버튼을 클릭하여 댓글을 작성한다.

        } catch (WebDriverException e) {

        }
    }
    public void initBrowser() {
        driver = new WebClient(BrowserVersion.CHROME);
        driver.setAjaxController(new NicelyResynchronizingAjaxController());
        driver.getOptions().setThrowExceptionOnScriptError(false);
        driver.getOptions().setThrowExceptionOnFailingStatusCode(false);
        driver.getOptions().setJavaScriptEnabled(true);
        driver.getOptions().setRedirectEnabled(true);
        driver.getCookieManager().setCookiesEnabled(true);
        driver.getOptions().setCssEnabled(false);
        driver.waitForBackgroundJavaScript(1000);
    }
}
