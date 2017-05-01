package com.twillmott.traktbrowser.service;

import com.twillmott.traktbrowser.domain.Season;
import com.twillmott.traktbrowser.domain.TvShow;
import com.twillmott.traktbrowser.repository.EpisodeRepository;
import com.twillmott.traktbrowser.repository.SeasonRepository;
import com.twillmott.traktbrowser.repository.TvShowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Service relating to all things TV Show / TvShow
 *
 * Created by tomw on 01/05/2017.
 */
@Component
public class TvService {

    // Inejcted dependencies
    private TvShowRepository tvShowRepository;
    private SeasonRepository seasonRepository;
    private EpisodeRepository episodeRepository;

    @Autowired
    TvService (TvShowRepository tvShowRepository, SeasonRepository seasonRepository, EpisodeRepository episodeRepository) {
        this.tvShowRepository = tvShowRepository;
        this.seasonRepository = seasonRepository;
        this.episodeRepository = episodeRepository;
    }


    /**
     * Get all series from the users databse.
     * If watched is true, only watched data will be returned.
     */
    public List<TvShow> getAllUserTvShows(boolean watched) {
        if (watched) {
            return null; // TODO implement this
        } else {
            return tvShowRepository.findAll();
        }
    }


    /**
     * Get all seasons for a given tvShow.
     * If watched is true, only watched data will be returned.
     */
    public List<Season> getAllSeasonsForTvShow(TvShow tvShow, boolean watched) {
        if (watched) {
            return null; // TODO implement this
        } else {
            return seasonRepository.findByTvShow(tvShow);
        }
    }

}
