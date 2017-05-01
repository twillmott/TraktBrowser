package com.twillmott.traktbrowser.domain;

import org.dozer.Mapping;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * An episode of a TV show.
 * Created by tomw on 16/04/2017.
 */
@Entity
public class Episode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private int episodeNumber;

    private String title;

    @OneToOne
    @JoinColumn(name = "season_id")
    private Season season;

    private String overview;

    @OneToOne
    @JoinColumn(name = "external_ids_id")
    private ExternalIds externalIds;

    @OneToOne
    @JoinColumn(name = "images_id")
    private Images images;

    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime lastWatched;

    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime lastCollected;

    @Mapping(value = "first_aired")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime airedOn;

    private int plays;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPlays() {
        return plays;
    }

    public void setPlays(int plays) {
        this.plays = plays;
    }

    public Episode() {}

    public Episode(int episodeNumber, String title, Season season, String overview, ExternalIds externalIds, Images images, DateTime lastWatched, DateTime lastCollected, DateTime airedOn) {
        this.episodeNumber = episodeNumber;
        this.title = title;
        this.season = season;
        this.overview = overview;
        this.externalIds = externalIds;
        this.images = images;
        this.lastWatched = lastWatched;
        this.lastCollected = lastCollected;
        this.airedOn = airedOn;
    }

    public int getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(int episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
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

    public DateTime getLastWatched() {
        return lastWatched;
    }

    public void setLastWatched(DateTime lastWatched) {
        this.lastWatched = lastWatched;
    }

    public DateTime getLastCollected() {
        return lastCollected;
    }

    public void setLastCollected(DateTime lastCollected) {
        this.lastCollected = lastCollected;
    }

    public DateTime getAiredOn() {
        return airedOn;
    }

    public void setAiredOn(DateTime airedOn) {
        this.airedOn = airedOn;
    }
}
