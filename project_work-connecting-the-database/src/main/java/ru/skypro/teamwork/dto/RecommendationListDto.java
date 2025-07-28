package ru.skypro.teamwork.dto;

import java.util.List;
import java.util.UUID;

public class RecommendationListDto {

    private UUID userId;
    private List<RecommendationDto> recommendations;

    public RecommendationListDto(UUID userId, List<RecommendationDto> recommendations) {
        this.userId = userId;
        this.recommendations = recommendations != null ? recommendations : List.of();
    }

    public UUID getUserId() {
        return userId;
    }

    public List<RecommendationDto> getRecommendations() {
        return recommendations;
    }
}
