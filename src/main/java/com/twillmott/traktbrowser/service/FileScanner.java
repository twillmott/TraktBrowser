package com.twillmott.traktbrowser.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.twillmott.traktbrowser.domain.Episode;
import com.twillmott.traktbrowser.domain.Season;
import com.twillmott.traktbrowser.domain.TvShow;
import com.twillmott.traktbrowser.model.FileEpisode;
import com.twillmott.traktbrowser.repository.EpisodeRepository;
import com.twillmott.traktbrowser.repository.SeasonRepository;
import com.twillmott.traktbrowser.repository.TvShowRepository;
import com.twillmott.traktbrowser.utils.StringUtils;
import javafx.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Service to handle scanning of files on the drive.
 *
 * With help from https://github.com/tvrenamer/tvrenamer/blob/master/src/main/org/tvrenamer/controller/TVRenamer.java
 *
 * Created by tomwi on 31/12/2016.
 */
@Component
public class FileScanner {

    private static Log LOG = LogFactory.getLog(FileScanner.class);

    // Regex used to scan TV shows and Media.
    public static final String[] REGEX = {
            "(.+?\\d{4}\\W\\D*?)[sS]?(\\d\\d?)\\D*?(\\d\\d).*\\D(\\d+[pk]).*", // this one works for titles with years
            "(.+?\\W\\D*?)[sS](\\d\\d?)[eE](\\d\\d?).*\\D(\\d+[pk]).*", // this one matches SXXEXX
            "(.+\\W\\D*?)[sS](\\d\\d?)\\D*?[eE](\\d\\d).*\\D(\\d+[pk]).*", // this one matches sXX.eXX
            "(.+\\W\\D*?)(\\d\\d?)\\D+(\\d\\d).*\\D(\\d+[pk]).*", // this one matches everything else
            "(.+\\W+)(\\d\\d?)(\\d\\d).*\\D(\\d+[pk]).*" // truly last resort
    };

    public static final Pattern[] COMPILED_REGEX = new Pattern[REGEX.length * 2];

    static {
        for (int i = 0; i < REGEX.length * 2; i++) {
            if (i / REGEX.length == 0){
                COMPILED_REGEX[i] = Pattern.compile(REGEX[i]);
            } else {
                COMPILED_REGEX[i] = Pattern.compile(REGEX[i - REGEX.length].replace(".*\\D(\\d+[pk])", ""));
            }
        }
    }

    // Injected dependencies
    TraktService traktService;
    private TvShowRepository tvShowRepository;
    private SeasonRepository seasonRepository;
    private EpisodeRepository episodeRepository;

    FileScanner (TraktService traktService,
                 TvShowRepository tvShowRepository,
                 SeasonRepository seasonRepository,
                 EpisodeRepository episodeRepository) {
        this.traktService = traktService;
        this.tvShowRepository = tvShowRepository;
        this.seasonRepository = seasonRepository;
        this.episodeRepository = episodeRepository;
    }

    /**
     * Get all TV shows within a directory, expressed as a {@link FileEpisode}.
     */
    public List<Episode> getTvShows() {

        List<FileEpisode> episodeFiles = Lists.newArrayList();

        try(Stream<Path> paths = Files.walk(Paths.get("D:\\TV Shows"))) {
            paths.forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    FileEpisode episode = parseEpisodeFilename(filePath.toString());
                    if (episode != null) {
                        episodeFiles.add(episode);
                        LOG.info("Episode detected: " + episode.toString());
                    }
                }
            });
        } catch (Exception e) {
            LOG.error(e.fillInStackTrace());
        }

        // Go through each of the episodes we've found, and search for it in Trakt.
        List<Episode> episodes = Lists.newArrayList();
        Map<String, Season> seasons = Maps.newHashMap(); // Key is the show id with season number appended
        Map<Integer, TvShow> tvShows = Maps.newHashMap();

        // TODO combine this with the above. Actually, we might need to stay like this to minimise trakt API calls.
        for (FileEpisode fileEpisode : episodeFiles) {

            Pair<Episode, Integer> episodeShowPair = traktService.searchForEpisode(fileEpisode);
            Episode episode = episodeShowPair.getKey();
            Integer showId = episodeShowPair.getValue();
            // Set collected time to now. Collected times will be updated from trakt later.
            episode.setLastCollected(DateTime.now());

            // We now need to link the season and TV show to the episode. The season and tv show may not yet be in the database.
            TvShow tvShow;
            // Check if we've already loaded the show
            if (tvShows.containsKey(showId)) {
                tvShow = tvShows.get(showId);
            // If not, get the show from the database
            } else if (!tvShowRepository.findByExternalIds_TraktId(showId).isEmpty()){
                tvShow = tvShowRepository.findByExternalIds_TraktId(showId).get(0);
            // If not in db, make a new one
            } else {
                tvShow = traktService.getTvShow(showId);
                tvShowRepository.save(tvShow);
                tvShows.put(showId, tvShow);
            }

            // Now do the same with seasons
            Season season;
            String seasonKey = showId.toString().concat(Integer.toString(fileEpisode.getSeasonNumber()));
            if (seasons.containsKey(seasonKey)) {
                season = seasons.get(seasonKey);
            } else if (!seasonRepository.findByTvShowAndSeasonNumber(tvShow, fileEpisode.getSeasonNumber()).isEmpty()) {
                season = seasonRepository.findByTvShowAndSeasonNumber(tvShow, fileEpisode.getSeasonNumber()).get(0);
            } else {
                season = traktService.getSeason(tvShow.getExternalIds().getTraktId(), fileEpisode.getSeasonNumber());
                season.setTvShow(tvShow);
                seasonRepository.save(season);
                seasons.put(seasonKey, season);
            }

            episode.setSeason(season);
            episodeRepository.save(episode);



            episodes.add(episode);
        }

        return episodes;
    }

    /**
     * Decide if a file path is a TV show episode. If it is, return a
     * {@link FileEpisode}.
     */
    private static FileEpisode parseEpisodeFilename(String filePath) {

        File file = new File(filePath);
        String fileName = stripJunk(insertShowNameIfNeeded(file));
        int index = 0;
        Matcher matcher = null;

        while (index < COMPILED_REGEX.length) {
            matcher = COMPILED_REGEX[index++].matcher(fileName);
            if (matcher.matches() && matcher.groupCount() == 4) {
                String show = matcher.group(1);
                show = StringUtils.replacePunctuation(show).toLowerCase();

                int season = Integer.parseInt(matcher.group(2));
                int episode = Integer.parseInt(matcher.group(3));
                String resolution = matcher.group(4);

                FileEpisode ep = new FileEpisode(show, season, episode, resolution, file);
                return ep;
            } else if (matcher.matches() && matcher.groupCount() == 3){
                String show = matcher.group(1);
                show = StringUtils.replacePunctuation(show).toLowerCase();

                int season = Integer.parseInt(matcher.group(2));
                int episode = Integer.parseInt(matcher.group(3));
                String resolution = "";

                FileEpisode ep = new FileEpisode(show, season, episode, resolution, file);
                return ep;
            }
        }
        return null;
    }

    /**
     * Strip unwanted words/phrases from a string.
     */
    private static String stripJunk(String input) {
        String output = input;
        output = removeLast(output, "hdtv");
        output = removeLast(output, "dvdrip");
        return output;

    }

    /**
     * Remove the last instance of string from another string.
     */
    private static String removeLast(String input, String match) {
        int idx = input.toLowerCase().lastIndexOf(match);
        if (idx > 0) {
            input = input.substring(0, idx);
        }
        return input;
    }

    /**
     * Append the show name to the front of a file name if it's required.
     */
    private static String insertShowNameIfNeeded(File file) {
        String fileName = file.getName();
        if (fileName.matches("[sS]\\d\\d?[eE]\\d\\d?.*")) {
            String parentName = file.getParentFile().getName();
            if (parentName.toLowerCase().startsWith("season")) {
                parentName = file.getParentFile().getParentFile().getName();
            }
            LOG.info("Appending parent directory '" + parentName + "' to filename '" + fileName + "'");
            return parentName + " " + fileName;
        } else {
            return fileName;
        }
    }
}
