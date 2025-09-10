package ru.skypro.teamwork.service.ruleset;

import ru.skypro.teamwork.dto.RecommendationDto;

import java.util.Optional;
import java.util.UUID;

/**
 * Контракт для набора правил (rule set), который агрегирует несколько отдельных правил
 * и возвращает рекомендацию, если все условия выполнены.
 */
public interface RecommendationRuleSetService {

    /**
     * Применяет набор правил к пользователю.
     * @param userId идентификатор пользователя
     * @return Optional с рекомендацией, если условия выполнены, иначе пусто
     */
    Optional<RecommendationDto> applyRule(UUID userId);
}
