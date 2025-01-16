package com.genaipeople.researcher.services.rss;

import com.genaipeople.researcher.model.RssFeed;
import com.genaipeople.researcher.util.Config;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RssExtractionService {
    private final SyndFeedInput syndFeedInput;
    private final Config config;

    public RssExtractionService() {
        this.syndFeedInput = new SyndFeedInput();
        this.config = Config.getInstance();
    }

    // Constructor for testing
    RssExtractionService(SyndFeedInput syndFeedInput, Config config) {
        this.syndFeedInput = syndFeedInput;
        this.config = config;
    }

    public List<RssFeed> extractRssFeeds(String url) {
        List<RssFeed> rssFeeds = new ArrayList<>();
        
        try {
            URL feedUrl = new URL(url);
            SyndFeed feed = syndFeedInput.build(new XmlReader(feedUrl));
            
            List<SyndEntry> entries = feed.getEntries();
            int maxItems = config.getIntValue("rss.max.items", 50);
            
            // Limit the number of items based on configuration
            entries = entries.stream()
                           .limit(maxItems)
                           .collect(Collectors.toList());
            
            for (SyndEntry entry : entries) {
                RssFeed rssFeed = new RssFeed();
                rssFeed.setTitle(entry.getTitle());
                rssFeed.setDescription(entry.getDescription() != null ? 
                    entry.getDescription().getValue() : "");
                rssFeed.setPublishDate(entry.getPublishedDate());
                rssFeed.setLink(entry.getLink());
                rssFeed.setAuthor(entry.getAuthor());
                
                rssFeeds.add(rssFeed);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract RSS feed from URL: " + url, e);
        }
        
        return rssFeeds;
    }

    public List<RssFeed> extractAllConfiguredFeeds() {
        List<RssFeed> allFeeds = new ArrayList<>();
        List<String> urls = config.getListValue("rss.feed.urls");
        
        for (String url : urls) {
            try {
                allFeeds.addAll(extractRssFeeds(url));
            } catch (Exception e) {
                // Log the error but continue with other feeds
                System.err.println("Error extracting feed from " + url + ": " + e.getMessage());
            }
        }
        
        return allFeeds;
    }
}
