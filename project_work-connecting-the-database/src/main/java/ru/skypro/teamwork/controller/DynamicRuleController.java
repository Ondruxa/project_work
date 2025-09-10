package ru.skypro.teamwork.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.teamwork.dto.DynamicRuleDto;
import ru.skypro.teamwork.dto.DynamicRuleListResponse;
import ru.skypro.teamwork.dto.DynamicRuleRequest;
import ru.skypro.teamwork.dto.RuleStatsResponseDto;
import ru.skypro.teamwork.service.DynamicRuleService;

import java.util.UUID;

/**
 * REST-контроллер для управления динамическими правилами рекомендаций.
 * <p>
 * Базовый путь: /rule
 * <ul>
 *   <li>POST /rule — создание нового правила продукта.</li>
 *   <li>GET /rule — получение списка всех правил.</li>
 *   <li>DELETE /rule/{productId} — удаление правила по productId.</li>
 * </ul>
 */
@RestController
@RequestMapping("/rule")
@RequiredArgsConstructor
public class DynamicRuleController {

    private final DynamicRuleService dynamicRuleService;

    /**
     * Создаёт новое динамическое правило для продукта.
     *
     * @param request тело запроса с данными правила (productId, имя, текст, условия)
     * @return созданное правило
     */
    @PostMapping
    public DynamicRuleDto createRule(@RequestBody DynamicRuleRequest request) {
        return dynamicRuleService.createRule(request);
    }

    /**
     * Возвращает список всех динамических правил.
     *
     * @return список правил
     */
    @GetMapping
    public DynamicRuleListResponse getAllRules() {
        return dynamicRuleService.getAllRules();
    }

    /**
     * Удаляет правило по ID продукта.
     *
     * @param productId UUID продукта (как строка), для которого нужно удалить правило
     */
    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRule(@PathVariable String productId) {
        dynamicRuleService.deleteRuleByProductId(productId);
    }
}