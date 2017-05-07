package com.twillmott.traktbrowser.controller;

import com.google.common.collect.Lists;
import com.twillmott.traktbrowser.model.TvShow;
import com.twillmott.traktbrowser.service.FileScanner;
import com.twillmott.traktbrowser.service.TraktService;
import com.twillmott.traktbrowser.service.TvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Controller for handling interaction with the TV Show/TvShow page.
 * Created by tomwi on 30/12/2016.
 */
@Controller
public class TvShowController {

    // Injected dependencies
    private TraktService traktService;
    private TvService tvService;
    private FileScanner fileScanner;

    @Autowired
    TvShowController(TraktService traktService, TvService tvService, FileScanner fileScanner) {
        this.traktService = traktService;
        this.tvService = tvService;
        this.fileScanner = fileScanner;
    }


    /**
     * Mapping for the root of the tv shows page.
     */
    @RequestMapping(value = "/tvshows", method = RequestMethod.GET)
    public String greeting(Model model) {
//        FileScanner fs = new FileScanner();
//        fs.getTvShows();
//        traktService.synchronizeTvShowWatchStatus();

        // Scan for tv shows on the drive
        fileScanner.getTvShows();

        // Populate the screen with all watched shows
        List<com.twillmott.traktbrowser.domain.TvShow> tvShows = tvService.getAllUserTvShows(false);
        List<TvShow> tvShowModels = Lists.newArrayList();

        for ( com.twillmott.traktbrowser.domain.TvShow tvShow : tvShows) {
            TvShow tvShowModel = new TvShow();
            tvShowModel.setName(tvShow.getTitle());
            tvShowModel.setWatched(tvShow.getLastWatched() != null);
            tvShowModel.setSeasons(tvService.getAllSeasonsForTvShow(tvShow, false).size());
            tvShowModels.add(tvShowModel);
        }

        model.addAttribute("tvShow", tvShowModels);

        return "tvshows";
    }

    /**
     * Mapping for the request button.
     */
    @RequestMapping(value = "/tvshows/refresh.htm", method = RequestMethod.GET)
    @Async
    public String refreshLibrary(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        traktService.synchronizeTvShowWatchStatus();
        // Redirect back to the TV shows page
        return "redirect:/tvshows.html";
    }
}
