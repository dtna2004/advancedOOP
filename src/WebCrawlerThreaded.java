public class WebCrawlerThreaded {

    private Map<String, Integer> visitedUrls;
    private Deque<String> urlsQueue;
    private Instant startTime;
    private final int maxDepth = 4;
    private final int maxUrlsPerPage = 10;

    public WebCrawlerThreaded(Instant start) {
        visitedUrls = new HashMap<>();
        urlsQueue = new LinkedList<>();
        startTime = start;
    }

    public void crawl(String rootUrl) {
        urlsQueue.addLast(rootUrl);
        visitedUrls.put(rootUrl, 1);

        while (!urlsQueue.isEmpty()) {
            String url = urlsQueue.removeFirst();
            int depth = visitedUrls.get(url);

            if (depth < maxDepth) {
                try {
                    URL urlObject = new URL(url);
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlObject.openStream()));
                    String inputLine = in.readLine();
                    String rawHtml = "";

                    while(inputLine != null){
                        rawHtml += inputLine;
                        inputLine = in.readLine();
                    }

                    in.close();
                    parseAndAddUrls(rawHtml, depth);
                } catch (IOException e) {}
            }
        }
    }

    private void parseAndAddUrls(String rawHtml, int depth) {
        String urlPattern = "((\\/wiki\\/)+[^\\s\\.\\#\\:\"]+[\\w])\"";
        Pattern pattern = Pattern.compile(urlPattern);
        Matcher matcher = pattern.matcher(rawHtml);

        int cntUrlsPerPage = 0;

        while (matcher.find()) {
            String newUrl = matcher.group(1);
            newUrl = "https://en.wikipedia.org" + newUrl;

            if (!visitedUrls.containsKey(newUrl)) {
                urlsQueue.addLast(newUrl);
                visitedUrls.put(newUrl, depth+1);
                cntUrlsPerPage += 1;

                if (cntUrlsPerPage >= maxUrlsPerPage) {
                    break;
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        long startTime = System.nanoTime();
        Instant start = Instant.now();
        WebCrawlerThreaded crawler = new WebCrawlerThreaded(start);

        // Tạo và khởi chạy các luồng để crawl web
        CrawlThread thread1 = new CrawlThread(crawler, "https://en.wikipedia.org/wiki/Travelling_salesman_problem");
        CrawlThread thread2 = new CrawlThread(crawler, "https://en.wikipedia.org/wiki/Some_other_starting_url");

        thread1.start();
        thread2.start();

        // Đợi cho tất cả các luồng kết thúc
        thread1.join();
        thread2.join();

        long endTime = System.nanoTime();
        long totalTime = endTime - startTime;
        System.out.println("Visited " + crawler.visitedUrls.size() +" Urls in " + totalTime/1000000 +" ms");
    }
}
