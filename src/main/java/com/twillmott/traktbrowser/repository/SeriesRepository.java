package com.twillmott.traktbrowser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Dadtabase repository for {@link com.twillmott.traktbrowser.entity.Series}.
 * Created by tomw on 16/04/2017.
 */
@Repository
public interface SeriesRepository extends JpaRepository<com.twillmott.traktbrowser.entity.Series, Long>{
}
