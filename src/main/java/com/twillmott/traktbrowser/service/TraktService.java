package com.twillmott.traktbrowser.service;

import com.uwetrottmann.trakt5.TraktV2;
import com.uwetrottmann.trakt5.entities.AccessToken;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import retrofit2.Response;

import java.io.IOException;

/**
 * The class that handles all communication with Trakt.
 *
 * Created by tomwi on 07/12/2016.
 */
public class TraktService {

    // The access token that holds the trakt authentication.
    private AccessToken accessToken;

    // Specific to this application.
    private static String CLIENT_ID = "033be5bf9ab8eb35954fdc9a5aaf5008768705bd13928431be2530c7005bcf0d";
    private static String CLIENT_SECRET = "6c314152776403a9acadf53ee156de377bfe650a8cd66109836b15beb7fb0f00";
    private static String REDIRECT_URL = "http://127.0.0.1:8080/browser/auth";

    // Library used to communicate with trakt.
    private TraktV2 trakt = new TraktV2(CLIENT_ID, CLIENT_SECRET, REDIRECT_URL);

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
        return accessToken != null;
    }
}
