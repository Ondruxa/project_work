package ru.skypro.teamwork.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skypro.teamwork.dto.DynamicRuleDto;
import ru.skypro.teamwork.dto.DynamicRuleListResponse;
import ru.skypro.teamwork.dto.DynamicRuleRequest;
import ru.skypro.teamwork.model.DynamicRule;
import ru.skypro.teamwork.repository.DynamicRuleRepository;
import ru.skypro.teamwork.service.DynamicRuleService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DynamicRuleServiceImpl implements DynamicRuleService {

    private final DynamicRuleRepository repository;
    private final ObjectMapper objectMapper;

    @Override
    public DynamicRuleDto createRule(DynamicRuleRequest request) {
        try {
            String ruleJson = objectMapper.writeValueAsString(request.getRule());
            DynamicRule entity = new DynamicRule(
                    null,
                    request.getProductName(),
                    request.getProductId(),
                    request.getProductText(),
                    ruleJson
            );
            DynamicRule saved = repository.save(entity);
            DynamicRuleDto dto = new DynamicRuleDto();
            dto.setId(saved.getId());
            dto.setProductName(saved.getProductName());
            dto.setProductId(saved.getProductId());
            dto.setProductText(saved.getProductText());
            dto.setRule(request.getRule());
            return dto;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка сериализации правила", e);
        }
    }

    @Override
    public DynamicRuleListResponse getAllRules() {
        List<DynamicRuleDto> rules = repository.findAll().stream().map(entity -> {
            DynamicRuleDto dto = new DynamicRuleDto();
            dto.setId(entity.getId());
            dto.setProductName(entity.getProductName());
            dto.setProductId(entity.getProductId());
            dto.setProductText(entity.getProductText());
            try {
                dto.setRule(objectMapper.readValue(entity.getRule(), List.class));
            } catch (JsonProcessingException e) {
                dto.setRule(null);
            }
            return dto;
        }).collect(Collectors.toList());
        DynamicRuleListResponse response = new DynamicRuleListResponse();
        response.setData(rules);
        return response;
    }

    @Override
    public void deleteRuleByProductId(String productId) {
        repository.findAll().stream()
                .filter(rule -> rule.getProductId().equals(productId))
                .findFirst()
                .ifPresent(rule -> repository.deleteById(rule.getId()));
    }
}