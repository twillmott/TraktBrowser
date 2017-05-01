package com.twillmott.traktbrowser.service;

import com.twillmott.traktbrowser.domain.Episode;
import com.twillmott.traktbrowser.domain.Season;
import com.twillmott.traktbrowser.domain.Series;
import com.twillmott.traktbrowser.repository.EpisodeRepository;
import com.twillmott.traktbrowser.repository.SeasonRepository;
import com.twillmott.traktbrowser.repository.SeriesRepository;
import groovy.util.logging.Commons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Service relating to all things TV Show / Series
 *
 * Created by tomw on 01/05/2017.
 */
@Component
public class TvService {

    SeriesRepository seriesRepository;
    SeasonRepository seasonRepository;
    EpisodeRepository episodeRepository;

    @Autowired
    TvService (SeriesRepository seriesRepository, SeasonRepository seasonRepository, EpisodeRepository episodeRepository) {
        this.seriesRepository = seriesRepository;
        this.seasonRepository = seasonRepository;
        this.episodeRepository = episodeRepository;
    }


    /**
     * Get all series from the users databse.
     * If watched is true, only watched data will be returned.
     */
    public List<Series> getAllUserSeries(boolean watched) {
        if (watched) {
            return null; // TODO implement this
        } else {
            return seriesRepository.findAll();
        }
    }


    /**
     * Get all seasons for a given series.
     * If watched is true, only watched data will be returned.
     */
    public List<Season> getAllSeasonsForSeries(Series series, boolean watched) {
        if (watched) {
            return null; // TODO implement this
        } else {
            return seasonRepository.findBySeries(series);
        }
    }

}
