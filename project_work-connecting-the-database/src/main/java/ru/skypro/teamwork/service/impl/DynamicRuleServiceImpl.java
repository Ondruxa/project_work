package ru.skypro.teamwork.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.teamwork.dto.DynamicRuleDto;
import ru.skypro.teamwork.dto.DynamicRuleListResponse;
import ru.skypro.teamwork.dto.DynamicRuleRequest;
import ru.skypro.teamwork.dto.RuleConditionDto;
import ru.skypro.teamwork.model.DynamicRule;
import ru.skypro.teamwork.model.RuleCondition;
import ru.skypro.teamwork.model.RuleConditionArgument;
import ru.skypro.teamwork.repository.DynamicRuleRepository;
import ru.skypro.teamwork.service.DynamicRuleService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DynamicRuleServiceImpl implements DynamicRuleService {

    private final DynamicRuleRepository repository;

    @Override
    @Transactional
    public DynamicRuleDto createRule(DynamicRuleRequest request) {
        DynamicRule entity = buildEntityFromRequest(request);
        DynamicRule saved = repository.save(entity);
        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DynamicRuleListResponse getAllRules() {
        List<DynamicRuleDto> data = repository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
        DynamicRuleListResponse resp = new DynamicRuleListResponse();
        resp.setData(data);
        return resp;
    }

    @Override
    @Transactional
    public void deleteRuleByProductId(String productId) {
        UUID uuid = UUID.fromString(productId);
        repository.deleteByProductId(uuid);
    }

    private DynamicRule buildEntityFromRequest(DynamicRuleRequest request) {
        DynamicRule entity = new DynamicRule();
        if (request.getProductId() != null && !request.getProductId().isBlank()) {
            entity.setProductId(UUID.fromString(request.getProductId()));
        } else {
            // fallback: временно генерируем productId, чтобы не нарушать NOT NULL
            entity.setProductId(UUID.randomUUID());
        }
        entity.setProductName(request.getProductName());
        entity.setProductText(request.getProductText());
        entity.setRule(buildConditions(request.getRule(), entity));
        return entity;
    }

    private List<RuleCondition> buildConditions(List<RuleConditionDto> dtos, DynamicRule parent) {
        if (dtos == null || dtos.isEmpty()) {
            return Collections.emptyList();
        }
        List<RuleCondition> result = new ArrayList<>(dtos.size());
        for (RuleConditionDto dto : dtos) {
            result.add(buildCondition(dto, parent));
        }
        return result;
    }

    private RuleCondition buildCondition(RuleConditionDto dto, DynamicRule parent) {
        RuleCondition condition = new RuleCondition();
        condition.setQuery(dto.getQuery());
        condition.setNegate(dto.isNegate());
        condition.setDynamicRule(parent);
        condition.setArguments(buildArguments(dto.getArguments(), condition));
        return condition;
    }

    private List<RuleConditionArgument> buildArguments(List<String> args, RuleCondition parent) {
        if (args == null || args.isEmpty()) {
            return Collections.emptyList();
        }
        List<RuleConditionArgument> result = new ArrayList<>(args.size());
        for (String raw : args) {
            RuleConditionArgument a = new RuleConditionArgument();
            a.setArgument(raw);
            a.setRuleCondition(parent);
            result.add(a);
        }
        return result;
    }

    private DynamicRuleDto toDto(DynamicRule entity) {
        DynamicRuleDto dto = new DynamicRuleDto();
        dto.setId(entity.getId());
        dto.setProductId(entity.getProductId());
        dto.setProductName(entity.getProductName());
        dto.setProductText(entity.getProductText());
        dto.setRule(mapConditions(entity.getRule()));
        return dto;
    }

    private List<RuleConditionDto> mapConditions(List<RuleCondition> conditions) {
        if (conditions == null || conditions.isEmpty()) {
            return Collections.emptyList();
        }
        return conditions.stream()
                .map(this::mapCondition)
                .collect(Collectors.toList());
    }

    private RuleConditionDto mapCondition(RuleCondition condition) {
        RuleConditionDto dto = new RuleConditionDto();
        dto.setQuery(condition.getQuery());
        dto.setNegate(condition.isNegate());
        dto.setArguments(extractArguments(condition));
        return dto;
    }

    private List<String> extractArguments(RuleCondition condition) {
        if (condition.getArguments() == null) {
            return Collections.emptyList();
        }
        return condition.getArguments()
                .stream()
                .map(RuleConditionArgument::getArgument)
                .toList();
    }
}