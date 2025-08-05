package ru.skypro.teamwork.dto;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class DynamicRuleDto {
    private UUID id;
    private String productName;
    private String productId;
    private String productText;
    private List<RuleConditionDto> rule;
}