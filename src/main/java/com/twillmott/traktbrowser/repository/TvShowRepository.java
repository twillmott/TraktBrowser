package com.twillmott.traktbrowser.repository;

import com.twillmott.traktbrowser.domain.TvShow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Dadtabase repository for {@link TvShow}.
 * Created by tomw on 16/04/2017.
 */
@Repository
public interface TvShowRepository extends JpaRepository<TvShow, Long> {

    List<TvShow> findByExternalIds_TraktId(Integer traktId);
}
