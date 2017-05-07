package com.twillmott.traktbrowser.domain;

import org.dozer.Mapping;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * Database domain to hold all details of a series (TV Show).
 * Created by tomw on 16/04/2017.
 */
@Entity
public class TvShow {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    /**
     * The main title of the series.
     */

    @Mapping(value = "title")
    private String title;


    @Column(length=1000000)
    @Mapping(value = "overview")
    private String overview;

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "external_ids_id")
    @Mapping(value = "ids")
    private ExternalIds externalIds;

    @OneToOne
    @JoinColumn(name = "images_id")
    private Images images;

    /**
     * ID of the next episode, if exists.
     * Cross referencing table
     */
    private Long nextEpisode;

    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime lastWatched;

    private boolean completed;

    private int plays;

    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Mapping(value = "first_aired")
    private DateTime airedOn;

    @Mapping(value = "network")
    private String network;

    @Mapping(value = "trailer")
    private String trailer;

    @Mapping(value = "rating")
    private double rating;

    public int getPlays() {
        return plays;
    }

    public void setPlays(int plays) {
        this.plays = plays;
    }

    public TvShow() {}

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

    public DateTime getLastWatched() {
        return lastWatched;
    }

    public void setLastWatched(DateTime lastWatched) {
        this.lastWatched = lastWatched;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public DateTime getAiredOn() {
        return airedOn;
    }

    public void setAiredOn(DateTime airedOn) {
        this.airedOn = airedOn;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getTrailer() {
        return trailer;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
