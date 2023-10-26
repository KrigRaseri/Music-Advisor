package com.umbrella.music_advisor.dto;

import lombok.Data;

@Data
public class CategoryPlaylistResponse {
    private Playlists playlists;

    @Data
    public static class Playlists {
        private Playlist[] items;
    }

    @Data
    public static class Playlist {
        private String name;
        private String href;
    }
}
