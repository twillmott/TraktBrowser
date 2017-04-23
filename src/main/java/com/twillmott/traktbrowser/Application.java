package com.twillmott.traktbrowser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * The spring boot application class used to
 * run this standalone application as an executable jar.
 * Spring runs an embedded tomcat server.
 *
 * Using this method means we don't need a web.xml :)
 *
 * Created using this tutorial: https://spring.io/guides/gs/serving-web-content/
 *
 * Created by tomwi on 07/12/2016.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.twillmott.traktbrowser"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
