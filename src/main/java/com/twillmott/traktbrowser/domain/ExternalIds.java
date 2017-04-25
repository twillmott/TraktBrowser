package com.twillmott.traktbrowser.domain;

import com.uwetrottmann.trakt5.entities.EpisodeIds;
import org.dozer.Mapping;

import javax.persistence.*;

/**
 * Entity to hold external identifier for a number of providers for both
 * tv shows and movies.
 * Created by tomw on 16/04/2017.
 */
@Entity
public class ExternalIds {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    @Mapping(value = "trakt")
    private Integer traktId;
    @Mapping(value = "tmdb")
    private Integer tmdbId;
    @Mapping(value = "tvdb")
    private Integer tvdbId;
    @Mapping(value = "tvrage")
    private Integer tvrageId;
    @Mapping(value = "imdb")
    private String imdbId;

    public ExternalIds() {}

    public ExternalIds(Integer traktId, Integer tmdbId, Integer tvdbId, Integer tvrageId, String imdbId) {
        this.traktId = traktId;
        this.tmdbId = tmdbId;
        this.tvdbId = tvdbId;
        this.tvrageId = tvrageId;
        this.imdbId = imdbId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getTraktId() {
        return traktId;
    }

    public void setTraktId(Integer traktId) {
        this.traktId = traktId;
    }

    public Integer getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(Integer tmdbId) {
        this.tmdbId = tmdbId;
    }

    public Integer getTvdbId() {
        return tvdbId;
    }

    public void setTvdbId(Integer tvdbId) {
        this.tvdbId = tvdbId;
    }

    public Integer getTvrageId() {
        return tvrageId;
    }

    public void setTvrageId(Integer tvrageId) {
        this.tvrageId = tvrageId;
    }

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public static EpisodeIds mapToTraktIds(ExternalIds input) {
        EpisodeIds output = new EpisodeIds();
        output.tvdb = input.getTvdbId();
        output.tvrage = input.getTvrageId();
        output.imdb = input.imdbId;
        output.trakt = input.getTraktId();
        output.tmdb = input.getTmdbId();
        return output;
    }

    public static ExternalIds mapFromTraktIds(EpisodeIds input) {
        ExternalIds output = new ExternalIds();
        output.setImdbId(input.imdb);
        output.setTmdbId(input.tmdb);
        output.setTraktId(input.trakt);
        output.setTvdbId(input.tvdb);
        output.setTvrageId(input.tvrage);
        return output;
    }
}
