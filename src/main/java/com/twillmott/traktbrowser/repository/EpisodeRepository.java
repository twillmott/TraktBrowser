package com.twillmott.traktbrowser.repository;

import com.twillmott.traktbrowser.domain.Episode;
import com.twillmott.traktbrowser.domain.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Database repository for an {@link Episode}.
 * Created by tomw on 16/04/2017.
 */
@Repository
public interface EpisodeRepository extends JpaRepository<Episode, Long> {

    List<Episode> findBySeasonAndEpisodeNumber(Season season, int episodeNumber);
}
