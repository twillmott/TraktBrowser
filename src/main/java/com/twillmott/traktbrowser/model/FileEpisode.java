package com.twillmott.traktbrowser.model;

import java.io.File;

/**
 * Model representing an episode file.
 *
 * Created by tomwi on 31/12/2016.
 */
public class FileEpisode {

    // The show name
    private final String showName;
    // The season number
    private final int seasonNumber;
    // The episode number
    private final int episodeNumber;
    // No idea
    private final String episodeResolution;
    // The file that is represented.
    private File file;

    public FileEpisode(String name, int season, int episode, String resolution, File f) {
        showName = name;
        seasonNumber = season;
        episodeNumber = episode;
        episodeResolution = resolution;
        file = f;
    }

    public String getShowName() {
        return showName;
    }

    public int getSeasonNumber() {
        return seasonNumber;
    }

    public int getEpisodeNumber() {
        return episodeNumber;
    }

    public String getEpisodeResolution() {
        return episodeResolution;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "FileEpisode { Show Name:" + showName + ", Season:" + seasonNumber + ", Episode:" + episodeNumber
                + ", File:" + file.getName() + " }";
    }
}
