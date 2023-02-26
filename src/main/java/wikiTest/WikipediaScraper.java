package wikiTest;

import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;

public class WikipediaScraper {
    
	private static final String WIKIPEDIA_DOMAIN = "en.wikipedia.org";
	private static final String WIKIPEDIA_URL_PATTERN = "https://%s/wiki/%s";
    
    public static void main(String[] args) throws Exception {

        final int MAX_CYCLES = 20;

        // Accepts a Wikipedia link - return/throw an error if the link is not a valid wiki link
        String wikiLink = "https://en.wikipedia.org/wiki/";
        if (!isValidWikiLink(wikiLink)) {
            throw new IllegalArgumentException("Invalid Wikipedia link: " + wikiLink);
        }
        
        // Accepts a valid integer between 1 to 20 - call it n
        Scanner input = new Scanner(System.in);
        System.out.println("Please enter how many cycles need process, number need to be btween 1 and 20");
        int n = input.nextInt();
        if (n < 1 || n > MAX_CYCLES) {
            throw new IllegalArgumentException("Invalid number of cycles: " + n);
        }
        
        // Scrape the link provided in Step 1, for all wiki links embedded in the page and store them in a data structure of your choice.
        Set<String> visitedLinks = new HashSet<>(); // to optimize the code not to visit any links you've already visited
        Queue<String> linksToVisit = new LinkedList<>();
        linksToVisit.offer(wikiLink);
        Set<String> foundLinks = new HashSet<>();
        
        for (int i = 0; i < n; i++) {
            int levelSize = linksToVisit.size();
            for (int j = 0; j < levelSize; j++) {
                String currentLink = linksToVisit.poll();
                if (visitedLinks.contains(currentLink)) {
                    continue; // skip links already visited
                }
                visitedLinks.add(currentLink);
                System.out.println("Visiting: " + currentLink);
                if 
                Document doc = Jsoup.connect(currentLink).get();
                Elements linkElements = doc.select("a[href^=/wiki/]");
                for (Element linkElement : linkElements) {
                    String link = linkElement.attr("href");
                    if (isValidWikiLink(link)) {
                        String absLink = "https://en.wikipedia.org" + link;
                        if (!visitedLinks.contains(absLink)) {
                            foundLinks.add(absLink);
                            linksToVisit.offer(absLink);
                        }
                    }
                }
            }
        }
        
        // Write the results ( all found links, total count, unique count ) to a JSON file.
        Gson gson = new Gson();
        String json = gson.toJson(foundLinks);
        int totalCount = visitedLinks.size();
        int uniqueCount = foundLinks.size();
        System.out.println("Total links visited: " + totalCount);
        System.out.println("Unique links found: " + uniqueCount);
        try (FileWriter writer = new FileWriter("output.json")) {
            writer.write("{\"links\":" + json + ",\"total\":" + totalCount + ",\"unique\":" + uniqueCount + "}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static boolean isValidWikiLink(String link) {

    	try {
            return isWikipediaLink(String.format(WIKIPEDIA_URL_PATTERN, WIKIPEDIA_DOMAIN, link));
        } catch (MalformedURLException e) {
            return false;
        }
    }
    
    private static boolean isWikipediaLink(String url) throws MalformedURLException {
        URL parsedUrl = new URL(url);
        return parsedUrl.getHost().endsWith(WIKIPEDIA_DOMAIN) && parsedUrl.getPath().startsWith("/wiki/");
    }

    
}
