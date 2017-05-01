package com.twillmott.traktbrowser.controller;

import com.google.common.collect.Lists;
import com.twillmott.traktbrowser.model.Series;
import com.twillmott.traktbrowser.repository.SeriesRepository;
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
 * Controller for handling interaction with the TV Show/Series page.
 * Created by tomwi on 30/12/2016.
 */
@Controller
public class SeriesController {

    TraktService traktService;
    TvService tvService;

    @Autowired
    SeriesController(TraktService traktService, TvService tvService) {
        this.traktService = traktService;
        this.tvService = tvService;
    }

    @RequestMapping(value = "/tvshows", method = RequestMethod.GET)
    public String greeting(Model model) {
//        FileScanner fs = new FileScanner();
//        fs.getTvShows();
//        traktService.synchronizeSeriesWatchStatus();

        // Scan for tv shows on the drive
        FileScanner fileScanner = new FileScanner();
        fileScanner.getTvShows();

        // Populate the screen with all watched shows
        List<com.twillmott.traktbrowser.domain.Series> userSeriesList = tvService.getAllUserSeries(false);
        List<Series> seriesModels = Lists.newArrayList();

        for ( com.twillmott.traktbrowser.domain.Series userSeries : userSeriesList) {
            Series seriesModel = new Series();
            seriesModel.setName(userSeries.getTitle());
            seriesModel.setWatched(userSeries.getLastWatched() != null);
            seriesModel.setSeasons(tvService.getAllSeasonsForSeries(userSeries, false).size());
            seriesModels.add(seriesModel);
        }

        model.addAttribute("series", seriesModels);

        return "tvshows";
    }

    @RequestMapping(value = "/tvshows/refresh.htm", method = RequestMethod.GET)
    @Async
    public String refreshLibrary(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        traktService.synchronizeSeriesWatchStatus();
        // Redirect back to the TV shows page
        return "redirect:";
    }
}
