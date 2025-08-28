package ru.skypro.teamwork.dto;

import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class RecommendationListDto {

    private final UUID userId;
    private final List<RecommendationDto> recommendations;

    public RecommendationListDto(UUID userId, List<RecommendationDto> recommendations) {
        this.userId = userId;
        this.recommendations = recommendations != null ? List.copyOf(recommendations) : List.of();
    }
}