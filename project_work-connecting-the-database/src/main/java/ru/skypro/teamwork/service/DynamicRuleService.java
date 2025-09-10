package ru.skypro.teamwork.service;

import ru.skypro.teamwork.dto.DynamicRuleDto;
import ru.skypro.teamwork.dto.DynamicRuleListResponse;
import ru.skypro.teamwork.dto.DynamicRuleRequest;

import java.util.UUID;

/**
 * Сервис управления динамическими правилами рекомендаций.
 * Позволяет создавать новое правило, получать список всех правил
 * и удалять правило по идентификатору связанного продукта.
 */
public interface DynamicRuleService {

    DynamicRuleDto createRule(DynamicRuleRequest request);

    DynamicRuleListResponse getAllRules();

    void deleteRuleByProductId(String productId);
}