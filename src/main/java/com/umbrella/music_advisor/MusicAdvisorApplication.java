package com.umbrella.music_advisor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class MusicAdvisorApplication {

    public static void main(String[] args) {
        SpringApplication.run(MusicAdvisorApplication.class, args);
    }
}
