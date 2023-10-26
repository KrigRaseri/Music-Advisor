package com.umbrella.music_advisor.config;

import com.umbrella.music_advisor.dto.CategoriesResponse;
import com.umbrella.music_advisor.dto.NewReleaseResponse;
import com.umbrella.music_advisor.service.SpotifyService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.Availability;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
@ShellComponent
public class SpringShellCommands {

    private final SpotifyConfig spotifyConfig;
    private final SpotifyService spotifyService;

    @Setter
    private static boolean auth = false;

    @ShellMethod("Authorize")
    public void auth() {
        System.out.println("use this link to request the access code:");
        System.out.println(spotifyConfig.getServerPath() + "/authorize?client_id=8e3f994331cd4138b3c67308635b4849&redirect_uri=http://localhost:8080&response_type=code");

        System.out.println("waiting for code...");
        spotifyService.accessTokenGetter();
    }

    @ShellMethod(value = "Get back featured playlists")
    public void featured() {
        spotifyService.spotifyGetFeatured().onErrorResume(e -> {
            throw new RuntimeException("Error occurred: " + e.getMessage());
        }).subscribe(
                featuredResponse -> Arrays.stream(featuredResponse.getPlaylists().getItems())
                        .map(p -> p.getName() + "\n" + p.getHref())
                        .forEach(x -> System.out.println(x + "\n")),
                error -> System.out.println("Error occurred: " + error.getMessage())
        );
    }

    @ShellMethod(key = "new", value = "Get back new releases")
    public void newReleases() {

        spotifyService.spotifyGetNewReleases()
                .onErrorResume(e -> {
                    throw new RuntimeException("Error occurred: " + e.getMessage());
                })
                .subscribe(newReleases -> {
                            NewReleaseResponse.Album[] albums = newReleases.getAlbums().getItems();
                            for (NewReleaseResponse.Album album : albums) {
                                System.out.println(album.getName());
                                System.out.println("[" + getArtistsList(album) + "]");
                                System.out.println(album.getHref());
                                System.out.println();
                            }
                        },
                        error -> System.out.println("Error occurred: " + error.getMessage())
                );
    }

    @ShellMethod("Get back categories")
    public void categories() {
        spotifyService.spotifyGetCategories()
                .onErrorResume(e -> {
                    throw new RuntimeException("Error occurred: " + e.getMessage());
                })
                .subscribe(
                        categoriesResponse -> Arrays.stream(categoriesResponse.getCategories().getItems())
                                .map(CategoriesResponse.Category::getName)
                                .forEach(System.out::println),
                        error -> System.out.println("Error occurred: " + error.getMessage())
                );
    }

    @ShellMethod("Get back playlists by category name")
    public void playlists(@Option String categoryName) {

        spotifyService.spotifyGetPlaylists(categoryName)
                .onErrorResume(e -> {
                    throw new RuntimeException("Error occurred: " + e.getMessage());
                })
                .subscribe(
                        playlistsResponse -> Arrays.stream(playlistsResponse.getPlaylists().getItems())
                                .map(p -> p.getName() + "\n" + p.getHref())
                                .forEach(x -> System.out.println(x + "\n")),
                        error -> System.out.println("Error occurred: " + error.getMessage())
                );
    }

    @ShellMethodAvailability({"new", "featured", "categories", "playlists"})
    public Availability availabilityCheck() {
        return auth
                ? Availability.available()
                : Availability.unavailable("you are not authorized");
    }

    private StringBuilder getArtistsList(NewReleaseResponse.Album album) {
        StringBuilder artistsList = new StringBuilder();
        NewReleaseResponse.Artist[] artists = album.getArtists();
        for (NewReleaseResponse.Artist artist : artists) {
            if (!artistsList.isEmpty()) {
                artistsList.append(", ");
            }
            artistsList.append(artist.getName());
        }
        return artistsList;
    }
}
