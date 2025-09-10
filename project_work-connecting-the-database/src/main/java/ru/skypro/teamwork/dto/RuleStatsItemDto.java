package ru.skypro.teamwork.dto;

/**
 * DTO одной записи статистики срабатывания правила.
 * <p>
 * Хранит идентификатор правила (rule_id) и количество срабатываний (count).
 */
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RuleStatsItemDto {
    private UUID rule_id;
    private long count;
}
