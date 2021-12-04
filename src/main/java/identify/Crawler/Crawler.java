package identify.Crawler;

public abstract class Crawler {

    public abstract void scrollRaw()  ;
    // Collects sentences. need
    //          writeToFileRaw("network-"+gallID+"-Raw.txt");
    //         buildNetwork("network-"+gallID+"-Raw.txt");
    //         writeToFile("network-"+gallID+".txt");


    public abstract void parseInfo()  ;
    // connects to each page
}
