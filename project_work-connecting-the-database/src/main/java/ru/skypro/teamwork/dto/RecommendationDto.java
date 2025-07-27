package ru.skypro.teamwork.dto;

import java.util.UUID;

public class RecommendationDto {
    private final UUID productId;
    private final String title;
    private final String description;

    public RecommendationDto(UUID productId, String title, String description) {
        this.productId = productId;
        this.title = title;
        this.description = description;
    }

    public UUID getProductId() {
        return productId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}