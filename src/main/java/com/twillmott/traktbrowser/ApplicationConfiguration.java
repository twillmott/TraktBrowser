package com.twillmott.traktbrowser;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.util.Properties;

/**
 * Configuration relating to this application.
 *
 * Created by tomw on 30/04/2017.
 */
@Configuration
public class ApplicationConfiguration {

    /**
     * Hibernate configuration.
     */
    Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.put("jadira.usertype.autoRegisterUserTypes", "true");
        return properties;
    }
}
