package org.schabi.newpipe.extractor.services.youtube;

import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import org.junit.jupiter.api.Test;
import org.schabi.newpipe.extractor.linkhandler.ChannelTabs;
import org.schabi.newpipe.extractor.linkhandler.ListLinkHandler;
import org.schabi.newpipe.extractor.search.filter.FilterItem;
import org.schabi.newpipe.extractor.services.youtube.extractors.YoutubeChannelExtractor;
import org.schabi.newpipe.extractor.services.youtube.linkHandler.YoutubeChannelLinkHandlerFactory;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.schabi.newpipe.extractor.ServiceList.YouTube;

public class YoutubeChannelExtractorTabsParsingTest {
    private static final String CHANNEL_ID = "channel/UCpodcastTabFixture0000000";

    @Test
    public void testPodcastsTabIsRecognizedFromTabUrlSuffix() throws Exception {
        final YoutubeChannelExtractor extractor = new YoutubeChannelExtractor(
                YouTube,
                YoutubeChannelLinkHandlerFactory.getInstance().fromId(CHANNEL_ID));

        setPrivateField(extractor, "redirectedChannelId", CHANNEL_ID);
        setPrivateField(extractor, "jsonResponse", buildTabsResponse());

        final List<ListLinkHandler> tabs = extractor.getTabs();
        assertTrue(tabs.stream().anyMatch(tab -> tab.getUrl().endsWith("/podcasts")));
        assertTrue(tabs.stream().anyMatch(tab -> {
            final List<FilterItem> filters = tab.getContentFilters();
            return !filters.isEmpty() && ChannelTabs.PODCASTS.equals(filters.get(0).getName());
        }));
    }

    private static JsonObject buildTabsResponse() throws Exception {
        return JsonParser.object().from("{\"contents\":{\"twoColumnBrowseResultsRenderer\":{\"tabs\":["
                + "{\"tabRenderer\":{\"endpoint\":{\"commandMetadata\":{\"webCommandMetadata\":{\"url\":\"/"
                + CHANNEL_ID
                + "/podcasts?view=57\"}}}}},"
                + "{\"tabRenderer\":{\"endpoint\":{\"commandMetadata\":{\"webCommandMetadata\":{\"url\":\"/"
                + CHANNEL_ID
                + "/playlists\"}}}}}"
                + "]}}}");
    }

    private static void setPrivateField(final Object target,
                                        final String fieldName,
                                        final Object value) throws Exception {
        final Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
