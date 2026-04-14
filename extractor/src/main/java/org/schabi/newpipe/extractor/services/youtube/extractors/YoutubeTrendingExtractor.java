package org.schabi.newpipe.extractor.services.youtube.extractors;

/*
 * Created by Christian Schabesberger on 12.08.17.
 *
 * Copyright (C) Christian Schabesberger 2018 <chris.schabesberger@mailbox.org>
 * YoutubeTrendingExtractor.java is part of NewPipe.
 *
 * NewPipe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NewPipe is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NewPipe.  If not, see <http://www.gnu.org/licenses/>.
 */

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonWriter;

import org.schabi.newpipe.extractor.Page;
import org.schabi.newpipe.extractor.ServiceList;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.downloader.Downloader;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.kiosk.KioskExtractor;
import org.schabi.newpipe.extractor.linkhandler.ListLinkHandler;
import org.schabi.newpipe.extractor.localization.TimeAgoParser;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.extractor.stream.StreamInfoItemsCollector;

import java.io.IOException;

import javax.annotation.Nonnull;

import static org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper.getJsonPostResponse;
import static org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper.getTextAtKey;
import static org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper.prepareDesktopJsonBuilder;
import static org.schabi.newpipe.extractor.utils.Utils.UTF_8;
import static org.schabi.newpipe.extractor.utils.Utils.isNullOrEmpty;

public class YoutubeTrendingExtractor extends KioskExtractor<StreamInfoItem> {
    private static final String KIOSK_RECOMMENDED_PODCASTS = "Recommended Podcasts";
    private static final String RECOMMENDED_LIVES_BROWSE_ID = "UC4R8DWoMoI7CAwX8_LjQHig";
    private static final String RECOMMENDED_PODCASTS_BROWSE_ID = "FEpodcasts_destination";
    private static final String RECOMMENDED_PODCASTS_PARAMS = "qgcCCAM=";
    private static final long RECOMMENDED_PODCASTS_MAX_ITEMS = 40;

    private JsonObject initialData;

    public YoutubeTrendingExtractor(final StreamingService service,
                                    final ListLinkHandler linkHandler,
                                    final String kioskId) {
        super(service, linkHandler, kioskId);
    }

    @Override
    public void onFetchPage(@Nonnull final Downloader downloader)
            throws IOException, ExtractionException {
        final byte[] body;
        if (KIOSK_RECOMMENDED_PODCASTS.equals(getId())) {
            body = JsonWriter.string(prepareDesktopJsonBuilder(getExtractorLocalization(),
                            getExtractorContentCountry())
                    .value("browseId", RECOMMENDED_PODCASTS_BROWSE_ID)
                    .value("params", RECOMMENDED_PODCASTS_PARAMS)
                    .done())
                    .getBytes(UTF_8);
        } else {
            body = JsonWriter.string(prepareDesktopJsonBuilder(getExtractorLocalization(),
                            getExtractorContentCountry())
                    .value("browseId", RECOMMENDED_LIVES_BROWSE_ID)
                    .done())
                    .getBytes(UTF_8);
        }

        initialData = getJsonPostResponse("browse", body, getExtractorLocalization());
    }

    @Override
    public InfoItemsPage<StreamInfoItem> getPage(final Page page) {
        return InfoItemsPage.emptyPage();
    }

    @Nonnull
    @Override
    public String getName() throws ParsingException {
        final JsonObject header = initialData.getObject("header");
        String name = null;
        if (header.has("feedTabbedHeaderRenderer")) {
            name = getTextAtKey(header.getObject("feedTabbedHeaderRenderer"), "title");
        } else if (header.has("c4TabbedHeaderRenderer")) {
            name = getTextAtKey(header.getObject("c4TabbedHeaderRenderer"), "title");
        } else if (header.has("carouselHeaderRenderer")) {
            name = header.getObject("carouselHeaderRenderer").getArray("contents").getObject(0)
                    .getObject("topicChannelDetailsRenderer").getObject("title").getString("simpleText");
        } else if (header.has("pageHeaderRenderer")) {
            name = header.getObject("pageHeaderRenderer").getString("pageTitle");
        }

        if (isNullOrEmpty(name)) {
            name = "Unknown";
        }
        return name;
    }

    @Nonnull
    @Override
    public InfoItemsPage<StreamInfoItem> getInitialPage() throws ParsingException {
        final StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());
        final TimeAgoParser timeAgoParser = getTimeAgoParser();
        final long maximumItems = KIOSK_RECOMMENDED_PODCASTS.equals(getId())
                ? RECOMMENDED_PODCASTS_MAX_ITEMS
                : Long.MAX_VALUE;
        final JsonObject tabContent = getTrendingTabRenderer().getObject("content");

        if (tabContent.has("richGridRenderer")) {
            tabContent.getObject("richGridRenderer")
                    .getArray("contents")
                    .stream()
                    .filter(JsonObject.class::isInstance)
                    .map(JsonObject.class::cast)
                    // Filter Trending shorts and Recently trending sections
                    .filter(content -> content.has("richItemRenderer"))
                    .map(content -> content.getObject("richItemRenderer")
                            .getObject("content"))
                    .filter(content -> content.has("videoRenderer"))
                    .map(content -> content.getObject("videoRenderer"))
                    .limit(maximumItems)
                    .forEachOrdered(videoRenderer -> collector.commit(
                            new YoutubeStreamInfoItemExtractor(videoRenderer, timeAgoParser)));
        } else if (tabContent.has("sectionListRenderer")) {
            tabContent.getObject("sectionListRenderer")
                    .getArray("contents")
                    .stream()
                    .filter(JsonObject.class::isInstance)
                    .map(JsonObject.class::cast)
                    .flatMap(content -> content.getObject("itemSectionRenderer")
                            .getArray("contents")
                            .stream())
                    .filter(JsonObject.class::isInstance)
                    .map(JsonObject.class::cast)
                    .map(content -> content.getObject("shelfRenderer"))
                    // Filter Trending shorts and Recently trending sections which have a title,
                    // contrary to normal trends
                    .filter(shelfRenderer -> !shelfRenderer.has("title"))
                    .flatMap(shelfRenderer -> shelfRenderer.getObject("content")
                            .getObject("expandedShelfContentsRenderer")
                            .getArray("items")
                            .stream())
                    .filter(JsonObject.class::isInstance)
                    .map(JsonObject.class::cast)
                    .map(item -> item.getObject("videoRenderer"))
                    .limit(maximumItems)
                    .forEachOrdered(videoRenderer -> collector.commit(
                            new YoutubeStreamInfoItemExtractor(videoRenderer, timeAgoParser)));
        }
        // try 1
        if(collector.getItems().isEmpty()) {
            tabContent.getObject("sectionListRenderer")
                    .getArray("contents")
                    .stream()
                    .filter(JsonObject.class::isInstance)
                    .map(JsonObject.class::cast)
                    .flatMap(content -> content.getObject("itemSectionRenderer")
                            .getArray("contents")
                            .stream())
                    .filter(JsonObject.class::isInstance)
                    .map(JsonObject.class::cast)
                    .map(content -> content.getObject("shelfRenderer"))
                    .flatMap(shelfRenderer -> shelfRenderer.getObject("content")
                            .getObject("horizontalListRenderer")
                            .getArray("items")
                            .stream())
                    .filter(JsonObject.class::isInstance)
                    .map(JsonObject.class::cast)
                    .map(item -> item.getObject("gridVideoRenderer"))
                    .limit(maximumItems)
                    .forEachOrdered(videoRenderer -> collector.commit(
                            new YoutubeStreamInfoItemExtractor(videoRenderer, timeAgoParser)));
        }
        // try 2
        if (collector.getItems().isEmpty()) {
            tabContent.getObject("richGridRenderer")
                    .getArray("contents")
                    .stream()
                    .filter(JsonObject.class::isInstance)
                    .map(JsonObject.class::cast)
                    // Filter Trending shorts and Recently trending sections
                    .filter(content -> content.has("richSectionRenderer"))
                    .map(content -> content.getObject("richSectionRenderer")
                            .getObject("content")
                            .getObject("richShelfRenderer"))
                    .flatMap(richShelfRenderer -> richShelfRenderer.getArray("contents")
                            .stream())
                    .filter(JsonObject.class::isInstance)
                    .map(JsonObject.class::cast)
                    .filter(content -> content.has("richItemRenderer"))
                    .map(content -> content.getObject("richItemRenderer")
                            .getObject("content")
                            .getObject("videoRenderer"))
                    .limit(maximumItems)
                    .forEachOrdered(videoRenderer -> collector.commit(
                            new YoutubeStreamInfoItemExtractor(videoRenderer, timeAgoParser)));
        }
        if (collector.getItems().isEmpty()) {
            throw new ParsingException("Could not get kiosk page: " + getId());
        }

        if (ServiceList.YouTube.getFilterTypes().contains("recommendations")) {
            collector.applyBlocking(ServiceList.YouTube.getFilterConfig());
        }
        return new InfoItemsPage<>(collector, null);
    }

    private JsonObject getTrendingTabRenderer() throws ParsingException {
        return initialData.getObject("contents")
                .getObject("twoColumnBrowseResultsRenderer")
                .getArray("tabs")
                .stream()
                .filter(JsonObject.class::isInstance)
                .map(JsonObject.class::cast)
                .map(tab -> tab.getObject("tabRenderer"))
                .filter(tabRenderer -> tabRenderer.getBoolean("selected"))
                // There should be at most one tab selected
                .findFirst()
                .orElseThrow(() -> new ParsingException("Could not get \"Now\" trending tab"));
    }
}
