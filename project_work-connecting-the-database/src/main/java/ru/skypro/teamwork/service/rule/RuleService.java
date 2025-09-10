package ru.skypro.teamwork.service.rule;

import java.util.UUID;

/**
 * Базовый контракт для сервиса проверки одного бизнес‑правила рекомендации.
 * Каждая реализация инкапсулирует собственную логику определения, выполняется ли условие
 * (например, наличие продукта, превышение суммы пополнений и т.д.).
 */
public interface RuleService {

    boolean applyRule(UUID userId);
}