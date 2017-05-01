package com.twillmott.traktbrowser.domain;

import javax.persistence.*;

/**
 * A TV Shows Season
 * Created by tomw on 16/04/2017.
 */
@Entity
public class Season {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne
    @JoinColumn(name = "external_ids_id")
    private ExternalIds externalIds;

    private int seasonNumber;

    /**
     * The tvShow which this season belongs to.
     */
    @OneToOne
    @JoinColumn(name = "series_id")
    private TvShow tvShow;

    @OneToOne
    @JoinColumn(name = "images_id")
    private Images images;


    public Season() {}

    public Season(ExternalIds externalIds, int seasonNumber, TvShow tvShow, Images images) {
        this.externalIds = externalIds;
        this.seasonNumber = seasonNumber;
        this.tvShow = tvShow;
        this.images = images;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ExternalIds getExternalIds() {
        return externalIds;
    }

    public void setExternalIds(ExternalIds externalIds) {
        this.externalIds = externalIds;
    }

    public int getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(int seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public TvShow getTvShow() {
        return tvShow;
    }

    public void setTvShow(TvShow tvShow) {
        this.tvShow = tvShow;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }
}


