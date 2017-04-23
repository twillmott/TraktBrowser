package com.twillmott.traktbrowser;

import com.twillmott.traktbrowser.domain.ExternalIds;
import com.uwetrottmann.trakt5.entities.EpisodeIds;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by tomw on 19/04/2017.
 */
public class RandomTest {

    @Test
    public void testFirst() {
        Mapper mapper = new DozerBeanMapper();

        EpisodeIds traktIds = new EpisodeIds();
        traktIds.tvrage = 2;
        traktIds.imdb = "IMDB";
        traktIds.tmdb = 3;
        traktIds.trakt = 4;
        traktIds.tvdb = 5;

        ExternalIds ids = mapper.map(traktIds, ExternalIds.class);

        assertEquals(traktIds.imdb,ids.getImdbId());



    }
}
