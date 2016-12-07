package com.twillmott.traktbrowser;


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
public class BrowserController {

    private int i = 0;

    @RequestMapping("/browser")
    public String greeting(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", i++);
        return "browser";
    }
}
