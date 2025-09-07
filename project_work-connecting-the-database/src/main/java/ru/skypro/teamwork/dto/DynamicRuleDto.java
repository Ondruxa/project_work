package ru.skypro.teamwork.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class DynamicRuleDto {

    private UUID id;

    @JsonProperty("product_id")
    private UUID productId;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("product_text")
    private String productText;

    @JsonProperty("rule")
    private List<RuleConditionDto> rule;
}