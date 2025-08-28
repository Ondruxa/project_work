package ru.skypro.teamwork.service;

import ru.skypro.teamwork.dto.DynamicRuleDto;
import ru.skypro.teamwork.dto.DynamicRuleListResponse;
import ru.skypro.teamwork.dto.DynamicRuleRequest;

public interface DynamicRuleService {

    DynamicRuleDto createRule(DynamicRuleRequest request);

    DynamicRuleListResponse getAllRules();

    void deleteRuleByProductId(String productId);
}