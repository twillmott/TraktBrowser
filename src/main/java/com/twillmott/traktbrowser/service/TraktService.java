package com.twillmott.traktbrowser.service;

import com.twillmott.traktbrowser.domain.Episode;
import com.twillmott.traktbrowser.domain.Season;
import com.twillmott.traktbrowser.domain.TvShow;
import com.twillmott.traktbrowser.model.FileEpisode;
import com.twillmott.traktbrowser.repository.AccessTokenRepository;
import com.twillmott.traktbrowser.repository.EpisodeRepository;
import com.twillmott.traktbrowser.repository.SeasonRepository;
import com.twillmott.traktbrowser.repository.TvShowRepository;
import com.uwetrottmann.trakt5.TraktV2;
import com.uwetrottmann.trakt5.entities.*;
import com.uwetrottmann.trakt5.enums.Extended;
import com.uwetrottmann.trakt5.enums.Type;
import javafx.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final Log LOG = LogFactory.getLog(TraktService.class);

    // The access token that holds the trakt authentication.
    private AccessToken accessToken;
    // Specific to this application.
    private static String CLIENT_ID = "033be5bf9ab8eb35954fdc9a5aaf5008768705bd13928431be2530c7005bcf0d";
    private static String CLIENT_SECRET = "6c314152776403a9acadf53ee156de377bfe650a8cd66109836b15beb7fb0f00";

    private static String REDIRECT_URL = "http://127.0.0.1:8080/overview/auth";

    // Library used to communicate with trakt.
    private TraktV2 trakt = new TraktV2(CLIENT_ID, CLIENT_SECRET, REDIRECT_URL);

    // Injected dependencies
    private AccessTokenRepository accessTokenDao;
    private Mapper mapper;
    private TvShowRepository tvShowRepository;
    private SeasonRepository seasonRepository;
    private EpisodeRepository episodeRepository;
    private TvService tvService;

    // TODO use the refresh token to refresh our access token.
    @Autowired
    public TraktService(
            AccessTokenRepository accessTokenDao,
            Mapper mapper,
            TvService tvService,
            TvShowRepository tvShowRepository,
            SeasonRepository seasonRepository,
            EpisodeRepository episodeRepository)
    {
        this.accessTokenDao = accessTokenDao;
        this.mapper = mapper;
        this.tvService = tvService;
        this.tvShowRepository = tvShowRepository;
        this.seasonRepository = seasonRepository;
        this.episodeRepository = episodeRepository;

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
     * Go through the database, and the watched status of all TvShow, Seasons and Episodes.
     */
    @Async
    public void synchronizeTvShowWatchStatus() {

        // Delete all watched info.
        tvService.deleteAll();

        // Get all the users watched TV Shows.
        List<BaseShow> watchedShows = getShowWatched();

        // Loop through all watched TV Shows
        for (BaseShow traktShow : watchedShows) {

            // Load the tvShow and mark it was watched.
//            TvShow tvShow = tvShowRepository.findByExternalIds_TraktId(traktShow.show.ids.trakt).get(0);
            TvShow tvShow = new TvShow();
            tvShow.setTitle(traktShow.show.title);
            tvShow.setLastWatched(traktShow.last_watched_at);
            tvShow.setPlays(traktShow.plays);
            tvShowRepository.save(tvShow);

            // Loop through all seasons
            for (com.uwetrottmann.trakt5.entities.BaseSeason traktSeason : traktShow.seasons) {

                // Trakt library doesn't give season watched info, so can't update that.
//                Season season = seasonRepository.findByTvShowAndSeasonNumber(tvShow, traktSeason.number).get(0);
                Season season = new Season();
                season.setSeasonNumber(traktSeason.number);
                season.setTvShow(tvShow);
                seasonRepository.save(season);

                // Loop through all the episodes in this season
                for (BaseEpisode traktEpisode : traktSeason.episodes) {
//                    Episode episode = episodeRepository.findBySeasonAndEpisodeNumber(season, traktEpisode.number).get(0);
                    Episode episode = new Episode();
                    episode.setEpisodeNumber(traktEpisode.number);
                    episode.setLastWatched(traktEpisode.last_watched_at);
                    episode.setPlays(traktEpisode.plays);
                    episode.setSeason(season);
                    episodeRepository.save(episode);
                }
            }
        }
    }


    /**
     * Get all the watched shows for the authorised user.
     */
    private List<BaseShow> getShowWatchlist() {
        try {
            Response<List<BaseShow>> response = trakt.users().watchlistShows(UserSlug.ME, Extended.FULLEPISODES).execute();
            if (response.isSuccessful()) {
                return response.body();
            }
            LOG.error(response.errorBody().string());
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
            LOG.error(response.errorBody().string());
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
            Response<List<BaseShow>> response = trakt.users().collectionShows(UserSlug.ME, Extended.FULL).execute();
            if (response.isSuccessful()) {
                return response.body();
            }
            LOG.error(response.errorBody().string());
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

    /**
     * Given a file episode, search for the trakt episode. This episode will also return the trakt id of the
     * @return A pair containing the episode itself, and the trakt id (integer).
     */
    public Pair<Episode, Integer> searchForEpisode(FileEpisode fileEpisode) {

        // Get the show ID
        Show show = new Show();
        try {
            Response<List<SearchResult>> response = trakt.search().textQuery(fileEpisode.getShowName(), Type.SHOW, null, null, null).execute();
            if (response.isSuccessful()) {
                show = response.body().get(0).show;
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        // Get the episode
        com.uwetrottmann.trakt5.entities.Episode traktEpisode;
        try {
            Response<com.uwetrottmann.trakt5.entities.Episode> response = trakt.episodes().summary(show.ids.trakt.toString(), fileEpisode.getSeasonNumber(), fileEpisode.getEpisodeNumber(), Extended.FULL).execute();
            if (response.isSuccessful()) {
                traktEpisode = response.body();
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        if (traktEpisode != null) {
            return new Pair<>(mapper.map(traktEpisode, Episode.class), show.ids.trakt);
        }
        return null;
    }

    /**
     * Get a {@link TvShow} for a given trakt id.
     */
    public TvShow getTvShow(Integer showId) {

        try {
            Response<Show> response = trakt.shows().summary(showId.toString(), Extended.FULL).execute();
            if (response.isSuccessful()) {
                return mapper.map(response.body(), TvShow.class);
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Get a {@link Season} for a given series and season number.
     */
    public Season getSeason(Integer showId, int seasonNumber) {

        try {
            Response<List<com.uwetrottmann.trakt5.entities.Season>> response = trakt.seasons().summary(showId.toString(), Extended.FULL).execute();
            if (response.isSuccessful()) {

                List<com.uwetrottmann.trakt5.entities.Season> seasons = response.body();

                for (com.uwetrottmann.trakt5.entities.Season season : seasons) {   // TODO Java8 Streams
                    if (season.number == seasonNumber) {
                        return mapper.map(season, Season.class);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
