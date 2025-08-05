package ru.skypro.teamwork.dto;

import lombok.Data;
import java.util.List;

@Data
public class DynamicRuleRequest {
    private String productName;
    private String productId;
    private String productText;
    private List<RuleConditionDto> rule;
}