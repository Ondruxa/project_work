package ru.skypro.teamwork.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class RecommendationDto {

/**
 * DTO отдельной рекомендации продукта пользователю.
 * Содержит идентификатор продукта и текстовые поля (title, description) для отображения.
 */
    private final UUID productId;
    private final String title;
    private final String description;
}