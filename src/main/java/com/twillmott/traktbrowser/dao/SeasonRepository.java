package com.twillmott.traktbrowser.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Database reposiroty for @{@link Season}
 * Created by tomw on 16/04/2017.
 */
@Repository
public interface SeasonRepository extends JpaRepository<Season, Long>{
}
