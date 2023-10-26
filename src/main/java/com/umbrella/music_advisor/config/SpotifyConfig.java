package com.umbrella.music_advisor.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Getter
@Setter
@Configuration
public class SpotifyConfig {

    @Value("${server.path}")
    private String serverPath;

    @Value("${app.path}")
    private String appPath;

    @Bean()
    WebClient authWebClient() {
        System.out.println("serverPath was called = " + serverPath);
        return WebClient.builder().baseUrl(serverPath).build();
    }

    @Bean()
    WebClient spotifyWebClient() {
        System.out.println("appPath was called = " + appPath);
        return WebClient.builder().baseUrl(appPath).build();
    }
}
