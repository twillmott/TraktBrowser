package com.twillmott.traktbrowser.domain;

import org.dozer.Mapping;

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

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "external_ids_id")
    @Mapping(value = "ids")
    private ExternalIds externalIds;

    @Mapping(value = "number")
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

    @Mapping(value = "episode_count")
    private int episodes;


    @Mapping(value = "rating")
    private double rating;


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

    public int getEpisodes() {
        return episodes;
    }

    public void setEpisodes(int episodes) {
        this.episodes = episodes;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}


