package com.twillmott.traktbrowser.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Database repository for an {@link Episode}.
 * Created by tomw on 16/04/2017.
 */
@Repository
public interface EpisodeRepository extends JpaRepository<Episode, Long>{
}
