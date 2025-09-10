package ru.skypro.teamwork.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Запрос на создание/обновление динамического правила.
 * Используется в POST /rule для приёма данных от клиента.
 */
@Getter
@Setter
public class DynamicRuleRequest {

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("product_id")
    private String productId;

    @JsonProperty("product_text")
    private String productText;

    @JsonProperty("rule")
    private List<RuleConditionDto> rule;
}