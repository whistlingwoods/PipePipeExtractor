package org.schabi.newpipe.extractor.services.youtube.linkHandler;

/*
 * Created by Christian Schabesberger on 12.08.17.
 *
 * Copyright (C) Christian Schabesberger 2018 <chris.schabesberger@mailbox.org>
 * YoutubeTrendingLinkHandlerFactory.java is part of NewPipe.
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

import org.schabi.newpipe.extractor.search.filter.FilterItem;

import static org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper.isInvidiousURL;
import static org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper.isYoutubeURL;

import org.schabi.newpipe.extractor.linkhandler.ListLinkHandlerFactory;
import org.schabi.newpipe.extractor.utils.Utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class YoutubeTrendingLinkHandlerFactory extends ListLinkHandlerFactory {

    private static final String TRENDING_ID = "Trending";
    private static final String RECOMMENDED_LIVES_ID = "Recommended Lives";
    private static final String RECOMMENDED_PODCASTS_ID = "Recommended Podcasts";
    private static final String TRENDING_URL = "https://www.youtube.com/feed/trending";
    private static final String RECOMMENDED_LIVES_URL =
            "https://www.youtube.com/channel/UC4R8DWoMoI7CAwX8_LjQHig";
    private static final String RECOMMENDED_PODCASTS_URL = "https://www.youtube.com/podcasts/videos";

    public String getUrl(final String id,
                         final List<FilterItem> contentFilters,
                         final List<FilterItem> sortFilter) {
        if (TRENDING_ID.equals(id)) {
            return TRENDING_URL;
        }

        if (RECOMMENDED_PODCASTS_ID.equals(id)) {
            return RECOMMENDED_PODCASTS_URL;
        }

        return RECOMMENDED_LIVES_URL;
    }

    @Override
    public String getId(final String url) {
        if (TRENDING_URL.equals(url)) {
            return TRENDING_ID;
        }

        if (RECOMMENDED_PODCASTS_URL.equals(url)
                || "https://www.youtube.com/podcasts".equals(url)) {
            return RECOMMENDED_PODCASTS_ID;
        }

        return RECOMMENDED_LIVES_ID;
    }

    @Override
    public boolean onAcceptUrl(final String url) {
        final URL urlObj;
        try {
            urlObj = Utils.stringToURL(url);
        } catch (final MalformedURLException e) {
            return false;
        }

        final String urlPath = urlObj.getPath();
        return Utils.isHTTP(urlObj) && (isYoutubeURL(urlObj) || isInvidiousURL(urlObj))
                && (urlPath.equals("/feed/trending")
                || urlPath.equals("/channel/UC4R8DWoMoI7CAwX8_LjQHig")
                || urlPath.equals("/podcasts")
                || urlPath.equals("/podcasts/")
                || urlPath.equals("/podcasts/videos")
                || urlPath.equals("/podcasts/videos/"));
    }
}
