package com.twillmott.traktbrowser.controller;

import com.google.common.collect.Lists;
import com.twillmott.traktbrowser.model.Series;
import com.twillmott.traktbrowser.service.FileScanner;
import com.twillmott.traktbrowser.service.TraktService;
import com.uwetrottmann.trakt5.entities.BaseShow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

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

    @Autowired
    SeriesController(TraktService traktService) {
        this.traktService = traktService;
    }

    @RequestMapping(value = "/tvshows", method = RequestMethod.GET)
    public String greeting(Model model) {
//        FileScanner fs = new FileScanner();
//        fs.getTvShows();
//        traktService.synchronizeSeries();

        Series series1 = new Series();
        series1.setName("Breaking Bad");
        series1.setSeasons(10);
        series1.setContinuing(true);
        series1.setWatched(false);

        Series series2 = new Series();
        series2.setName("New Girl");
        series2.setSeasons(12);
        series2.setContinuing(false);
        series2.setWatched(true);

        List<Series> series = Lists.newArrayList(series1, series2);

        model.addAttribute("series", series);

        return "tvshows";
    }

    @RequestMapping(value = "/tvshows/refresh.htm", method = RequestMethod.GET)
    @Async
    public String refreshLibrary(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        traktService.synchronizeSeries();
        // Redirect back to the TV shows page
        return "redirect:";
    }
}
