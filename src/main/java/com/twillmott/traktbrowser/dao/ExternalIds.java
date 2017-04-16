package com.twillmott.traktbrowser.dao;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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
    private Integer traktId;
    private Integer tmdbId;
    private Integer tvdbId;
    private Integer tvrageId;
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
}
