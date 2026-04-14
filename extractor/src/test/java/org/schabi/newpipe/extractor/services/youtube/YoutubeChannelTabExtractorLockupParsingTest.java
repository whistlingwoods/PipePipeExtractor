package org.schabi.newpipe.extractor.services.youtube;

import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import org.junit.jupiter.api.Test;
import org.schabi.newpipe.extractor.MultiInfoItemsCollector;
import org.schabi.newpipe.extractor.linkhandler.ChannelTabs;
import org.schabi.newpipe.extractor.services.youtube.extractors.YoutubeChannelTabExtractor;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.schabi.newpipe.extractor.ServiceList.YouTube;

public class YoutubeChannelTabExtractorLockupParsingTest {

    @Test
    public void testCollectItemHandlesRichItemLockupViewModel() throws Exception {
        final YoutubeChannelTabExtractor extractor = (YoutubeChannelTabExtractor) YouTube
                .getChannelTabExtractorFromId("UCpodcastTabFixture0000000", ChannelTabs.PODCASTS);

        final Method method = YoutubeChannelTabExtractor.class.getDeclaredMethod(
                "collectItem",
                MultiInfoItemsCollector.class,
                JsonObject.class,
                java.util.List.class);
        method.setAccessible(true);

        final MultiInfoItemsCollector collector = new MultiInfoItemsCollector(YouTube.getServiceId());
        method.invoke(extractor,
                collector,
                buildRichItemLockupContent(),
                Arrays.asList("Memoire Vive", "https://www.youtube.com/channel/UCTFUnkRlNBCHwE0oD_6PM7g"));

        assertEquals(1, collector.getItems().size());
    }

    @Test
    public void testCollectItemHandlesVideoLockupViewModel() throws Exception {
        final YoutubeChannelTabExtractor extractor = (YoutubeChannelTabExtractor) YouTube
                .getChannelTabExtractorFromId("UCpodcastTabFixture0000000", ChannelTabs.PODCASTS);

        final Method method = YoutubeChannelTabExtractor.class.getDeclaredMethod(
                "collectItem",
                MultiInfoItemsCollector.class,
                JsonObject.class,
                java.util.List.class);
        method.setAccessible(true);

        final MultiInfoItemsCollector collector = new MultiInfoItemsCollector(YouTube.getServiceId());
        method.invoke(extractor,
                collector,
                buildVideoLockupContent(),
                Arrays.asList("Memoire Vive", "https://www.youtube.com/channel/UCTFUnkRlNBCHwE0oD_6PM7g"));

        assertEquals(1, collector.getItems().size());
    }

    @Test
    public void testCollectItemHandlesNestedRichItemShelfContent() throws Exception {
        final YoutubeChannelTabExtractor extractor = (YoutubeChannelTabExtractor) YouTube
                .getChannelTabExtractorFromId("UCpodcastTabFixture0000000", ChannelTabs.PODCASTS);

        final Method method = YoutubeChannelTabExtractor.class.getDeclaredMethod(
                "collectItem",
                MultiInfoItemsCollector.class,
                JsonObject.class,
                java.util.List.class);
        method.setAccessible(true);

        final MultiInfoItemsCollector collector = new MultiInfoItemsCollector(YouTube.getServiceId());
        method.invoke(extractor,
                collector,
                buildNestedRichItemShelfLockupContent(),
                Arrays.asList("Memoire Vive", "https://www.youtube.com/channel/UCTFUnkRlNBCHwE0oD_6PM7g"));

        assertEquals(1, collector.getItems().size());
    }

    private static JsonObject buildRichItemLockupContent() throws Exception {
        return JsonParser.object().from("{\"richItemRenderer\":{\"content\":{\"lockupViewModel\":{"
                + "\"contentType\":\"LOCKUP_CONTENT_TYPE_PODCAST\","
                + "\"contentId\":\"PL1234567890ABCDE\","
                + "\"contentImage\":{\"collectionThumbnailViewModel\":{\"primaryThumbnail\":{"
                + "\"thumbnailViewModel\":{\"image\":{\"sources\":[{\"url\":\"https://i.ytimg.com/vi/test/default.jpg\"}]},"
                + "\"overlays\":[{\"thumbnailOverlayBadgeViewModel\":{\"thumbnailBadges\":[{\"thumbnailBadgeViewModel\":{\"text\":\"12\"}}]}}]"
                + "}}}},"
                + "\"metadata\":{\"lockupMetadataViewModel\":{"
                + "\"title\":{\"content\":\"Test podcast\"},"
                + "\"metadata\":{\"contentMetadataViewModel\":{\"metadataRows\":[{}]}}"
                + "}}"
                + "}}}}");
    }

    private static JsonObject buildVideoLockupContent() throws Exception {
        return JsonParser.object().from("{\"lockupViewModel\":{"
                + "\"contentType\":\"LOCKUP_CONTENT_TYPE_VIDEO\","
                + "\"contentId\":\"dQw4w9WgXcQ\","
                + "\"contentImage\":{\"thumbnailViewModel\":{"
                + "\"image\":{\"sources\":[{\"url\":\"https://i.ytimg.com/vi/dQw4w9WgXcQ/default.jpg\"}]},"
                + "\"overlays\":[]"
                + "}},"
                + "\"metadata\":{\"lockupMetadataViewModel\":{"
                + "\"title\":{\"content\":\"Episode 1\"},"
                + "\"metadata\":{\"contentMetadataViewModel\":{\"metadataRows\":[]}}"
                + "}}"
                + "}}");
    }

    private static JsonObject buildNestedRichItemShelfLockupContent() throws Exception {
        return JsonParser.object().from("{\"richItemRenderer\":{\"content\":{\"shelfRenderer\":{"
                + "\"content\":{\"horizontalListRenderer\":{\"items\":[{\"lockupViewModel\":{"
                + "\"contentType\":\"LOCKUP_CONTENT_TYPE_PODCAST\","
                + "\"contentId\":\"PL1234567890ABCDE\","
                + "\"contentImage\":{\"collectionThumbnailViewModel\":{\"primaryThumbnail\":{"
                + "\"thumbnailViewModel\":{\"image\":{\"sources\":[{\"url\":\"https://i.ytimg.com/vi/test/default.jpg\"}]},"
                + "\"overlays\":[{\"thumbnailOverlayBadgeViewModel\":{\"thumbnailBadges\":[{\"thumbnailBadgeViewModel\":{\"text\":\"12\"}}]}}]"
                + "}}}},"
                + "\"metadata\":{\"lockupMetadataViewModel\":{"
                + "\"title\":{\"content\":\"Test podcast\"},"
                + "\"metadata\":{\"contentMetadataViewModel\":{\"metadataRows\":[{}]}}"
                + "}}"
                + "}}]}}}}}}}");
    }
}
