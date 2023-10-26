package com.umbrella.music_advisor.service;

import com.umbrella.music_advisor.config.SpringShellCommands;
import com.umbrella.music_advisor.dto.CategoriesResponse;
import com.umbrella.music_advisor.dto.CategoryPlaylistResponse;
import com.umbrella.music_advisor.dto.NewReleaseResponse;
import com.umbrella.music_advisor.dto.SpotifyTokenResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
@Service
public class SpotifyService {

    private final WebClient authWebClient;
    private final WebClient spotifyWebClient;

    @Getter
    @Setter
    private String code;
    private String accessToken;

    @Async
    public void accessTokenGetter() {
        long startTime = System.currentTimeMillis();
        long timeout = 10000;

        while (true) {
            if (System.currentTimeMillis() - startTime > timeout) {
                log.error("Timeout");
                break;
            }

            if (code != null) {
                spotifyRequestAccessToken(code)
                        .onErrorResume(e -> {
                            throw new RuntimeException("Error occurred: " + e.getMessage());
                        })
                        .subscribe(
                                token -> {
                                    accessToken = token.getAccess_token();
                                    SpringShellCommands.setAuth(true);
                                    log.info("Success");
                                },
                                error -> log.error("Error occurred: " + error.getMessage())
                        );
                break;
            }
        }
    }


    public Mono<SpotifyTokenResponse> spotifyRequestAccessToken(String code) {

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("code", code);
        requestBody.add("client_id", "8e3f994331cd4138b3c67308635b4849");
        requestBody.add("client_secret", "5b1ce1036af04c4a83c812b3975350a2");
        requestBody.add("redirect_uri", "http://localhost:8080");

        return authWebClient
                .post()
                .uri("/api/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(requestBody))
                .retrieve()
                .bodyToMono(SpotifyTokenResponse.class);
    }


    public Mono<NewReleaseResponse> spotifyGetNewReleases() {
        return spotifyWebClient
                .get()
                .uri("/v1/browse/new-releases")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(NewReleaseResponse.class);
    }


    public Mono<CategoriesResponse> spotifyGetCategories() {
        return spotifyWebClient
                .get()
                .uri("/v1/browse/categories")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(CategoriesResponse.class);
    }


    public Mono<CategoryPlaylistResponse> spotifyGetPlaylists(@RequestParam String category_id) {
        return spotifyWebClient
                .get()
                .uri("/v1/browse/categories/{category_id}/playlists", category_id)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(CategoryPlaylistResponse.class);
    }

    public Mono<CategoryPlaylistResponse> spotifyGetFeatured() {
        return spotifyWebClient
                .get()
                .uri("/v1/browse/featured-playlists")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(CategoryPlaylistResponse.class);
    }
}
