package ru.skypro.teamwork.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO агрегированной статистики по всем правилам.
 * <p>
 * Содержит коллекцию отдельных элементов статистики {@link RuleStatsItemDto}.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RuleStatsResponseDto {
    private List<RuleStatsItemDto> stats;
}
