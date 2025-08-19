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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DynamicRuleServiceImpl implements DynamicRuleService {

    private final DynamicRuleRepository repository;

    @Override
    @Transactional
    public DynamicRuleDto createRule(DynamicRuleRequest request) {
        DynamicRule entity = new DynamicRule();
        entity.setProductName(request.getProductName());
        entity.setProductId(request.getProductId());
        entity.setProductText(request.getProductText());

        List<RuleCondition> conditions = new ArrayList<>();
        if (request.getRule() != null) {
            for (RuleConditionDto dto : request.getRule()) {
                RuleCondition condition = new RuleCondition();
                condition.setQuery(dto.getQuery());
                condition.setNegate(dto.isNegate());
                condition.setDynamicRule(entity);

                List<RuleConditionArgument> argumentEntities = new ArrayList<>();
                if (dto.getArguments() != null) {
                    for (String arg : dto.getArguments()) {
                        RuleConditionArgument argument = new RuleConditionArgument();
                        argument.setArgument(arg);
                        argument.setRuleCondition(condition);
                        argumentEntities.add(argument);
                    }
                }
                condition.setArguments(argumentEntities);

                conditions.add(condition);
            }
        }
        entity.setRule(conditions);

        DynamicRule saved = repository.save(entity);
        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DynamicRuleListResponse getAllRules() {
        List<DynamicRuleDto> data = repository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        DynamicRuleListResponse resp = new DynamicRuleListResponse();
        resp.setData(data);
        return resp;
    }

    @Override
    @Transactional
    public void deleteRuleByProductId(String productId) {
        repository.deleteByProductId(productId);
    }

    private DynamicRuleDto toDto(DynamicRule entity) {
        DynamicRuleDto dto = new DynamicRuleDto();
        dto.setId(entity.getId());
        dto.setProductName(entity.getProductName());
        dto.setProductId(entity.getProductId());
        dto.setProductText(entity.getProductText());

        List<RuleConditionDto> ruleDtos = new ArrayList<>();
        if (entity.getRule() != null) {
            for (RuleCondition condition : entity.getRule()) {
                RuleConditionDto c = new RuleConditionDto();
                c.setQuery(condition.getQuery());

                if (condition.getArguments() != null) {
                    List<String> args = condition.getArguments().stream()
                            .map(RuleConditionArgument::getArgument)
                            .collect(Collectors.toList());
                    c.setArguments(args);
                } else {
                    c.setArguments(new ArrayList<>());
                }
                c.setNegate(condition.isNegate());
                ruleDtos.add(c);
            }
        }
        dto.setRule(ruleDtos);
        return dto;
    }
}