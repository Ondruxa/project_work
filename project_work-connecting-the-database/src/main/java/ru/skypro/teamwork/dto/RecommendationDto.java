package ru.skypro.teamwork.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class RecommendationDto {
    private final UUID productId;
    private final String title;
    private final String description;

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