package com.twillmott.traktbrowser.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;

/**
 * Database access layer for the Trakt access token table.
 * Created by tomwi on 30/12/2016.
 */
@Repository
public interface AccessTokenRepository extends JpaRepository<AccessToken, Long> {
}
