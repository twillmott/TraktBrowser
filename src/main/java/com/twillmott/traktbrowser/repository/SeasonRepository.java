package com.twillmott.traktbrowser.repository;

import com.twillmott.traktbrowser.domain.Season;
import com.twillmott.traktbrowser.domain.TvShow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Database reposiroty for @{@link Season}
 * Created by tomw on 16/04/2017.
 */
@Repository
public interface SeasonRepository extends JpaRepository<Season, Long>{

    List<Season> findByTvShowAndSeasonNumber(TvShow tvShow, int seasonNumber);
    List<Season> findByTvShow(TvShow tvShow);
}
