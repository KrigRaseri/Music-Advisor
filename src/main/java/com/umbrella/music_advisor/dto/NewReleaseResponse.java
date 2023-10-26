package com.umbrella.music_advisor.dto;

import lombok.Data;

@Data
public class NewReleaseResponse {

    private Albums albums;

    @Data
    public static class Albums {
        private Album[] items;
    }

    @Data
    public static class Album {
        private String name;
        private Artist[] artists;
        private String href;
    }

    @Data
    public static class Artist {
        private String name;
    }
}
