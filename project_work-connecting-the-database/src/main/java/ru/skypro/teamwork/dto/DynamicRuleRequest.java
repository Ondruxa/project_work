package ru.skypro.teamwork.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
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