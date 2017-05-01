package com.twillmott.traktbrowser.service;

import com.google.common.collect.Lists;
import com.twillmott.traktbrowser.model.FileEpisode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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
public class FileScanner {

    private static Log log = LogFactory.getLog(FileScanner.class);

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

    /**
     * Get all TV shows within a directory, expressed as a {@link FileEpisode}.
     */
    public static List<FileEpisode> getTvShows() {

        List<FileEpisode> files = Lists.newArrayList();

        try(Stream<Path> paths = Files.walk(Paths.get("D:\\TV Shows"))) {
            paths.forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    FileEpisode episode = parseEpisodeFilename(filePath.toString());
                    if (episode != null) {
                        files.add(episode);
                        log.info("Episode detected: " + episode.toString());
                    }
                }
            });
        } catch (Exception e) {
            log.error(e.fillInStackTrace());
        }
        return files;
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
            log.info("Appending parent directory '" + parentName + "' to filename '" + fileName + "'");
            return parentName + " " + fileName;
        } else {
            return fileName;
        }
    }
}
