package com.twillmott.traktbrowser.repository;

import com.twillmott.traktbrowser.domain.Series;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Dadtabase repository for {@link com.twillmott.traktbrowser.domain.Series}.
 * Created by tomw on 16/04/2017.
 */
@Repository
public interface SeriesRepository extends JpaRepository<com.twillmott.traktbrowser.domain.Series, Long> {

    List<Series> findByExternalIds_TraktId(Integer traktId);
}
