package org.schabi.newpipe.extractor.services.youtube;

import org.junit.jupiter.api.Test;
import org.schabi.newpipe.extractor.services.youtube.linkHandler.YoutubeTrendingLinkHandlerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class YoutubeTrendingLinkHandlerFactoryPodcastsTest {

    private static final YoutubeTrendingLinkHandlerFactory FACTORY =
            new YoutubeTrendingLinkHandlerFactory();

    @Test
    public void testRecommendedPodcastsUrlFromId() throws Exception {
        assertEquals("https://www.youtube.com/podcasts/videos",
                FACTORY.fromId("Recommended Podcasts").getUrl());
    }

    @Test
    public void testRecommendedPodcastsIdFromUrl() throws Exception {
        assertEquals("Recommended Podcasts",
                FACTORY.fromUrl("https://www.youtube.com/podcasts/videos").getId());
    }

    @Test
    public void testAcceptPodcastsUrls() throws Exception {
        assertTrue(FACTORY.acceptUrl("https://www.youtube.com/podcasts/videos"));
        assertTrue(FACTORY.acceptUrl("https://www.youtube.com/podcasts"));
    }
}
