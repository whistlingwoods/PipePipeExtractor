package org.schabi.newpipe.extractor.services.youtube;

import org.junit.jupiter.api.Test;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.linkhandler.ChannelTabs;
import org.schabi.newpipe.extractor.services.youtube.linkHandler.YoutubeChannelTabLinkHandlerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class YoutubeChannelTabLinkHandlerFactoryTest {
    @Test
    public void testPodcastsTabSuffix() throws ParsingException {
        assertEquals("/podcasts", YoutubeChannelTabLinkHandlerFactory.getUrlSuffix(ChannelTabs.PODCASTS));
    }

    @Test
    public void testUnsupportedTabSuffix() {
        assertThrows(ParsingException.class,
                () -> YoutubeChannelTabLinkHandlerFactory.getUrlSuffix("unsupported-tab"));
    }
}
