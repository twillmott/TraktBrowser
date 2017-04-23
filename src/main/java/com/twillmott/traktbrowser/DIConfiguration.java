package com.twillmott.traktbrowser;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * A class used to define {@link Bean}s used for dependency injection.
 *
 * These beans will mainly come from external libraries.
 *
 * Created by tomw on 23/04/2017.
 */
@Configuration
public class DIConfiguration {

    @Bean
    public Mapper getMapper() {
        return new DozerBeanMapper();
    }

}
