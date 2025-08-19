package ru.skypro.teamwork.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class DynamicRuleDto {
    private UUID id;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("product_id")
    private String productId;

    @JsonProperty("product_text")
    private String productText;

    @JsonProperty("rule")
    private List<RuleConditionDto> rule;
}