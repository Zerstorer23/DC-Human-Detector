package identify.Objects;


public class Page {
    public String writer;
    public String title;
    public String link;
    public String realURL;
    public String id;

    public Page() {


    }

    public Page(String title, String link) {
        this.title = title;
        this.link = link;
    }
}
