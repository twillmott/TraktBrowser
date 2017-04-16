package com.twillmott.traktbrowser.repository;

import com.twillmott.traktbrowser.entity.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Database repository for the Trakt access token table.
 * Created by tomwi on 30/12/2016.
 */
@Repository
public interface AccessTokenRepository extends JpaRepository<AccessToken, Long> {
}
