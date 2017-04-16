package com.twillmott.traktbrowser;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.twillmott.traktbrowser.model.Series;
import com.twillmott.traktbrowser.service.FileScanner;
import com.twillmott.traktbrowser.service.TraktService;
import com.uwetrottmann.trakt5.entities.BaseShow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
}
