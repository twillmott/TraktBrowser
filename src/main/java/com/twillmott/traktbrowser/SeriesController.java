package com.twillmott.traktbrowser;

import com.twillmott.traktbrowser.service.FileScanner;
import com.twillmott.traktbrowser.service.TraktService;
import com.uwetrottmann.trakt5.entities.BaseShow;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Controller for handling interaction with the TV Show/Series page.
 * Created by tomwi on 30/12/2016.
 */
@Controller
public class SeriesController {

    TraktService traktService = new TraktService();

    @RequestMapping("/tvshows")
    public String greeting(Model model) {
        FileScanner fs = new FileScanner();
        fs.getTvShows();
        return null;
    }
}
