package ru.skypro.teamwork.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Статистика срабатываний динамического правила.
 * <p>
 * Отражает количество (count) активных срабатываний для конкретного правила (ruleId).
 */
@Entity
@Table(name = "rule_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class RuleStats {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "count", nullable = false)
    private int count = 0;

    @Column(name = "rule_id", nullable = false, unique = true)
    private UUID ruleId;

    /**
     * Метод создания новой статистики для правила с начальным значением count=0.
     *
     * @param ruleId идентификатор правила
     * @return новый объект статистики
     */
    public static RuleStats create(UUID ruleId) {
        RuleStats rs = new RuleStats();
        rs.ruleId = ruleId;
        rs.count = 0;
        return rs;
    }
}
