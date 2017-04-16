package com.twillmott.traktbrowser.service;

import com.twillmott.traktbrowser.repository.AccessTokenRepository;
import com.uwetrottmann.trakt5.TraktV2;
import com.uwetrottmann.trakt5.entities.AccessToken;
import com.uwetrottmann.trakt5.entities.BaseShow;
import com.uwetrottmann.trakt5.entities.UserSlug;
import com.uwetrottmann.trakt5.enums.Extended;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import retrofit2.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The class that handles all communication with Trakt.
 *
 * Created by tomwi on 07/12/2016.
 */
@Component
public class TraktService {

    // The access token that holds the trakt authentication.
    private AccessToken accessToken;

    // Specific to this application.
    private static String CLIENT_ID = "033be5bf9ab8eb35954fdc9a5aaf5008768705bd13928431be2530c7005bcf0d";
    private static String CLIENT_SECRET = "6c314152776403a9acadf53ee156de377bfe650a8cd66109836b15beb7fb0f00";
    private static String REDIRECT_URL = "http://127.0.0.1:8080/overview/auth";

    // Library used to communicate with trakt.
    private TraktV2 trakt = new TraktV2(CLIENT_ID, CLIENT_SECRET, REDIRECT_URL);

    AccessTokenRepository accessTokenDao;

    Log log = LogFactory.getLog(TraktService.class);

    // TODO use the refresh token to refresh our access token.
    @Autowired
    public TraktService(AccessTokenRepository accessTokenDao) {
        this.accessTokenDao = accessTokenDao;
        // Try getting the access token from the database. If we don't have one, we'll have to authenticate.

        if (!accessTokenDao.findAll().isEmpty()) {
            accessToken = accessTokenDao.findAll().get(0).mapToTraktToken();
            trakt.accessToken(accessToken.access_token);
            trakt.refreshToken(accessToken.refresh_token);
        }
    }


    /**
     * Initial stage of authentication. Build the URL.
     */
    public String buildAuthUrl() {
        try {
            OAuthClientRequest request = trakt.buildAuthorizationRequest("");
            return request.getLocationUri();
        } catch (OAuthSystemException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Second part of authentication to be used after the authentication code has been collected.
     */
    public AccessToken continueAuthentication(String code) {
        try {
            Response<AccessToken> accessTokenResponse = trakt.exchangeCodeForAccessToken(code);

            if (accessTokenResponse.isSuccessful()) {
                accessToken = accessTokenResponse.body();
                // Every time we add a new access token, we want to delete the existing on out of the database.
                accessTokenDao.deleteAll();
                accessTokenDao.save(com.twillmott.traktbrowser.entity.AccessToken.mapFromTraktToken(accessToken));
                return accessToken;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Whether or not this application is authenticated.
     */
    public boolean isAuthenticated() {
        return accessToken!=null && accessToken.access_token != null;
    }

    /**
     * @return All of this users watched shows.
     */
    public List<BaseShow> getWatchedShows() {
        try {
            Response<List<BaseShow>> response = trakt.users().watchedShows(UserSlug.ME, Extended.DEFAULT_MIN).execute();
            if (response.isSuccessful()) {
                return response.body();
            }
            log.error(response.errorBody().string());
            return new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
