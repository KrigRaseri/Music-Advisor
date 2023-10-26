package com.umbrella.music_advisor.dto;

import lombok.Data;

@Data
public class CategoriesResponse {

    private Categories categories;

    @Data
    public static class Categories {
        private Category[] items;
    }

    @Data
    public static class Category {
        private String id;
        private String name;
    }
}
