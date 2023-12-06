public class CrawlThread extends Thread {

    private WebCrawlerThreaded webCrawler;
    private String rootUrl;

    public CrawlThread(WebCrawlerThreaded webCrawler, String rootUrl) {
        this.webCrawler = webCrawler;
        this.rootUrl = rootUrl;
    }

    @Override
    public void run() {
        webCrawler.crawl(rootUrl);
    }
}
