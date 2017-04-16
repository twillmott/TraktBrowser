package com.twillmott.traktbrowser.dao;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Images to be used with movies and TV shows.
 * Currently, this table only stores URL references to images. TODO Change this to save the images themselves.
 * Created by tomw on 16/04/2017.
 */
public class Images {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    private String bannerUrl;
    private String thumbnailUrl;

    public Images() {}

    public Images(String bannerUrl, String thumbnailUrl) {
        this.bannerUrl = bannerUrl;
        this.thumbnailUrl = thumbnailUrl;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
}
