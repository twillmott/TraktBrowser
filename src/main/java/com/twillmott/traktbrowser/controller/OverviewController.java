package com.twillmott.traktbrowser.controller;


import com.twillmott.traktbrowser.service.TraktService;
import com.uwetrottmann.trakt5.entities.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Created using this tutorial: https://spring.io/guides/gs/serving-web-content/
 *
 * Created by tomwi on 06/12/2016.
 */
@Controller
public class OverviewController {

    private TraktService traktService;

    private int i = 0;

    @Autowired
    OverviewController(TraktService traktService){
        this.traktService = traktService;
    }

    /**
     * Main entry point to the overview application. Checks for authentication.
     * If authenticated, populates the pages with the users media.
     */
    @RequestMapping("/overview")
    public String greeting(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
        if (traktService.isAuthenticated()) {
            model.addAttribute("name", i++);
            return "overview";
        } else {
            return "redirect:" + traktService.buildAuthUrl();
        }
    }


    /**
     * Represents the second part of OAuth. This URL is redirected to from the trakt website.
     */
    @RequestMapping("/overview/auth")
    public String authSuccess(@RequestParam(value="code", required=true) String code) {
        AccessToken accessToken = traktService.continueAuthentication(code);

        if (accessToken == null) {
            return "redirect:authFailure"; // Authentication failure :(
        } else {
            return "redirect:"; // Redirect back to the main overview page.
        }
    }


    /**
     * Handle a trakt authentication failure.
     */
    @RequestMapping("/overview/authFailure")
    public String authFailure() {
        return null;
    }
}
