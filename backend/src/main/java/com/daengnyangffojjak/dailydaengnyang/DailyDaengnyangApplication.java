package com.daengnyangffojjak.dailydaengnyang;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class DailyDaengnyangApplication {

    private static final String APPLICATION_LOCATIONS = "spring.config.location="
            + "classpath:application.yml,"
            + " /home/ubuntu/daengnyang/prod-application.yml";

    public static void main(String[] args) {
        new SpringApplicationBuilder(DailyDaengnyangApplication.class)
                .properties(APPLICATION_LOCATIONS)
                .run(args);
    }

}
