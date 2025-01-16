package com.services;

import com.genaipeople.researcher.model.RssFeed;
import com.genaipeople.researcher.services.rss.RssExtractionService;
import com.genaipeople.researcher.util.Config;
import com.rometools.rome.feed.synd.*;
import com.rometools.rome.io.SyndFeedInput;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RssExtractionServiceTest {

    private RssExtractionService rssExtractionService;

    @Mock
    private SyndFeedInput mockSyndFeedInput;

    @Mock
    private Config mockConfig;

    @BeforeEach
    void setUp() {
        when(mockConfig.getValue("max.feed.items")).thenReturn("50" );
        when(mockConfig.getValue("rss.feed.urls")).thenReturn(
            "https://timesofindia.indiatimes.com/rssfeedstopstories.cms"
        );
        rssExtractionService = new RssExtractionService();
    }

    @Test
    void extractRssFeeds_Success() throws Exception {
        // Arrange
        String testUrl = "https://timesofindia.indiatimes.com/rssfeedstopstories.cms";        

        // Execute
        List<RssFeed> result = rssExtractionService.extractRssFeeds(testUrl);

        // Assert
        assertNotNull(result);
        assertTrue(result.size() > 1);
        
        RssFeed rssFeed = result.get(0);
        assertNotNull(rssFeed.getTitle());
        assertNotNull(rssFeed.getDescription());
        assertNotNull(rssFeed.getLink());
        assertNotNull(rssFeed.getPublishDate());
        assertNotNull(rssFeed.getAuthor());
    }

    @Test
    void extractRssFeeds_WithNullDescription() throws Exception {
        // Arrange
        String testUrl = "https://example.com/rss";
        
        // Create mock RSS entry with null description
        SyndEntry mockEntry = new SyndEntryImpl();
        mockEntry.setTitle("Test Title");
        mockEntry.setDescription(null);

        List<SyndEntry> entries = new ArrayList<>();
        entries.add(mockEntry);

        
        // Execute
        List<RssFeed> result = rssExtractionService.extractRssFeeds(testUrl);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("", result.get(0).getDescription());
    }

    @Test
    void extractRssFeeds_InvalidUrl() {
        // Arrange
        String invalidUrl = "invalid-url";

        // Execute & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            rssExtractionService.extractRssFeeds(invalidUrl);
        });

        assertTrue(exception.getMessage().contains("Failed to extract RSS feed from URL"));
    }

    @Test
    void extractRssFeeds_EmptyFeed() throws Exception {
        // Arrange
        String testUrl = "https://example.com/rss";
        
        // Mock behavior for empty feed
        
        // Execute
        List<RssFeed> result = rssExtractionService.extractRssFeeds(testUrl);

        // Add logging
        System.out.println("Result: " + result);    
        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void extractAllConfiguredFeeds_Success() {
        // Arrange
        String configuredUrls = "https://timesofindia.indiatimes.com/rssfeedstopstories.cms";
        when(mockConfig.getValue("rss.feed.urls")).thenReturn(configuredUrls);

        // Execute
        List<RssFeed> result = rssExtractionService.extractAllConfiguredFeeds();

        // Assert
        assertNotNull(result);
        // Add more specific assertions based on your test data
    }
} 