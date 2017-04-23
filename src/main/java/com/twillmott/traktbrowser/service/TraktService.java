package com.twillmott.traktbrowser.service;

import com.twillmott.traktbrowser.domain.Episode;
import com.twillmott.traktbrowser.domain.Season;
import com.twillmott.traktbrowser.domain.Series;
import com.twillmott.traktbrowser.repository.AccessTokenRepository;
import com.twillmott.traktbrowser.repository.SeriesRepository;
import com.uwetrottmann.trakt5.TraktV2;
import com.uwetrottmann.trakt5.entities.AccessToken;
import com.uwetrottmann.trakt5.entities.BaseShow;
import com.uwetrottmann.trakt5.entities.UserSlug;
import com.uwetrottmann.trakt5.enums.Extended;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import retrofit2.Response;

import java.io.IOException;
import java.util.*;

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

    // Injected dependencies
    AccessTokenRepository accessTokenDao;
    Mapper mapper;
    SeriesRepository seriesRepository;

    Log log = LogFactory.getLog(TraktService.class);

    // TODO use the refresh token to refresh our access token.
    @Autowired
    public TraktService(AccessTokenRepository accessTokenDao, Mapper mapper, SeriesRepository seriesRepository) {
        this.accessTokenDao = accessTokenDao;
        this.mapper = mapper;
        this.seriesRepository = seriesRepository;
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
                accessTokenDao.save(com.twillmott.traktbrowser.domain.AccessToken.mapFromTraktToken(accessToken));
                trakt.accessToken(accessToken.access_token);
                trakt.refreshToken(accessToken.refresh_token);
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
        return accessToken != null && accessToken.access_token != null;
    }

    /**
     * @return All of this users watched shows.
     */
    @Async
    public void synchronizeSeries() {
        // Get all of the users shows, with the minimum information.
        // Get the users watchlist
        List<BaseShow> watchlistShows = getShowWatchlist();
        // Get the users watched shows.
        List<BaseShow> watchedShows = getShowWatched();
        // Get the users collection.
        List<BaseShow> collectedShows = getShowCollection();

        // Merge all the shows in to one list.
        List<BaseShow> mergedShows = mergeShows(true, watchlistShows, watchedShows, collectedShows);

        List<Series> series = new ArrayList<>();
        for (BaseShow traktShow : mergedShows) {

            // Get the extra details of the show. Trakt doesn't give all details in the one api call.
            BaseShow fullShow = combineBaseShows(traktShow, getShowWatchedProgress(traktShow.show.ids.trakt.toString()));

            // Get all of the seasons for the show
            List<com.uwetrottmann.trakt5.entities.Season> traktSeasons = new ArrayList<>();
            try {
                traktSeasons = trakt.seasons().summary(traktShow.show.ids.trakt.toString(), Extended.DEFAULT_MIN).execute().body();
            } catch (Exception e) {
                log.error(e.getMessage());
            }

            List<Season> seasons = new ArrayList<>();
            // Go through each season and get its episodes
            for (com.uwetrottmann.trakt5.entities.Season traktSeason : traktSeasons) {
                if (traktSeason.number != 0) {

                    List<com.uwetrottmann.trakt5.entities.Episode> traktEpisodes = new ArrayList<>();
                    try {
                        traktEpisodes = trakt.seasons().season(traktShow.show.ids.trakt.toString(), traktSeason.number, Extended.DEFAULT_MIN).execute().body();
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }

                    List<Episode> episodeModels = new ArrayList<>();
                    for (com.uwetrottmann.trakt5.entities.Episode traktEpisode : traktEpisodes) {
//                        episodeModels.add(mapper.map(traktEpisode, Episode.class));
                    }

//                    seasons.add(mapper.map(traktSeason, Season.class));
                }
            }

//            series.add(mapper.map(fullShow, Series.class));
        }

        seriesRepository.save(series);

    }


    /**
     * Get all the watched shows for the authorised user.
     */
    private List<BaseShow> getShowWatchlist() {
        try {
            Response<List<BaseShow>> response = trakt.users().watchlistShows(UserSlug.ME, Extended.DEFAULT_MIN).execute();
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


    /**
     * Get all watched shows for the user.
     */
    private List<BaseShow> getShowWatched() {
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


    /**
     * Get all watched shows for the user.
     */
    private List<BaseShow> getShowCollection() {
        try {
            Response<List<BaseShow>> response = trakt.users().collectionShows(UserSlug.ME, Extended.DEFAULT_MIN).execute();
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


    /**
     * Given lists of shows, merge them to get a list of all shows in each list with no
     * duplicates sorted alphabetically if sorting is true.
     */
    private List<BaseShow> mergeShows(boolean sorting, final List<BaseShow>... shows) {
        final Map<String, BaseShow> showMap = new LinkedHashMap<>();

        // Go through all the lists and add them to the hash map in order to get only one instance.
        for (List<BaseShow> showList : shows) {
            for (BaseShow show : showList) {
                showMap.put(show.show.ids.trakt.toString().toLowerCase(), show);
            }
        }

        if (sorting) {
            // Return a sorted list
            return sortShowsAlphabetically( new ArrayList<>(showMap.values()));
        } else {
            // Return the unsorted list
            return new ArrayList<>(showMap.values());
        }
    }


    /**
     * Sort the list of shows alphabetically.
     */
    private List<BaseShow> sortShowsAlphabetically(List<BaseShow> shows) {
        Collections.sort(shows, new Comparator<BaseShow>() {
            public int compare(BaseShow v1, BaseShow v2) {
                return v1.show.title.compareTo(v2.show.title);
            }
        });
        return shows;
    }


    /**
     * Combine two base shows, if any of show A's properties are null, we will use
     * show B's property.
     */
    private BaseShow combineBaseShows(BaseShow showA, BaseShow showB) {
        BaseShow show = new BaseShow();

        show.next_episode = showA.next_episode == null ? showB.next_episode : showA.next_episode;
        show.show = showA.show == null ? showB.show : showA.show;
        show.aired = showA.aired == null ? showB.aired : showA.aired;
        show.completed = showA.completed == null ? showB.completed : showA.completed;
        show.hidden_seasons = showA.hidden_seasons == null ? showB.hidden_seasons : showA.hidden_seasons;
        show.last_collected_at = showA.last_collected_at == null ? showB.last_collected_at : showA.last_collected_at;
        show.last_watched_at = showA.last_watched_at == null ? showB.last_watched_at : showA.last_watched_at;
        show.listed_at = showA.listed_at == null ? showB.listed_at : showA.listed_at;
        show.next_episode = showA.next_episode == null ? showB.next_episode : showA.next_episode;
        show.plays = showA.plays == null ? showB.plays : showA.plays;
        show.completed = showA.completed == null ? showB.completed : showA.completed;

        return show;
    }


    /**
     * Get all watched shows for the user.
     */
    private BaseShow getShowWatchedProgress(String showId) {
        try {
            Response<BaseShow> response = trakt.shows().watchedProgress(showId, false, false, Extended.FULL).execute();
            if (response.isSuccessful()) {
                return response.body();
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
