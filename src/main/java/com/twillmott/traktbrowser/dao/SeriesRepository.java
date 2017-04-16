package com.twillmott.traktbrowser.dao;

import com.twillmott.traktbrowser.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Dadtabase repository for {@link Series}.
 * Created by tomw on 16/04/2017.
 */
@Repository
public interface SeriesRepository extends JpaRepository<Series, Long>{
}
