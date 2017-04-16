package com.twillmott.traktbrowser.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Database entity to hold all details of a series (TV Show).
 * Created by tomw on 16/04/2017.
 */
@Entity
public class Series {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    /**
     * The main title of the series.
     */
    private String title;

    private String overview;

    private ExternalIds externalIds;

    private Images images;

    /**
     * ID of the next episode, if exists.
     * Cross referencing table
     */
    private Long nextEpisode;

    public Series() {}

    public Series(String title, String overview, ExternalIds externalIds, Images images, Long nextEpisode) {
        this.title = title;
        this.overview = overview;
        this.externalIds = externalIds;
        this.images = images;
        this.nextEpisode = nextEpisode;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public ExternalIds getExternalIds() {
        return externalIds;
    }

    public void setExternalIds(ExternalIds externalIds) {
        this.externalIds = externalIds;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

    public Long getNextEpisode() {
        return nextEpisode;
    }

    public void setNextEpisode(Long nextEpisode) {
        this.nextEpisode = nextEpisode;
    }
}
