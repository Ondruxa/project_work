package ru.skypro.teamwork.dto;

import lombok.Getter;

import java.util.List;
import java.util.UUID;

/**
 * DTO списка рекомендаций конкретному пользователю.
 * Содержит идентификатор и ФИО пользователя, а также защищённый неизменяемый список рекомендаций.
 */
@Getter
public class RecommendationListDto {

    private final UUID userId;
    private final String firstName;
    private final String lastName;
    private final List<RecommendationDto> recommendations;

    public RecommendationListDto(UUID userId, String firstName, String lastName, List<RecommendationDto> recommendations) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.recommendations = recommendations != null ? List.copyOf(recommendations) : List.of();
    }
}