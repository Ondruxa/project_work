package ru.skypro.teamwork.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.teamwork.dto.DynamicRuleDto;
import ru.skypro.teamwork.dto.DynamicRuleListResponse;
import ru.skypro.teamwork.dto.DynamicRuleRequest;
import ru.skypro.teamwork.service.DynamicRuleService;

import java.util.UUID;

@RestController
@RequestMapping("/rule")
@RequiredArgsConstructor
public class DynamicRuleController {

    private final DynamicRuleService dynamicRuleService;

    @PostMapping
    public DynamicRuleDto createRule(@RequestBody DynamicRuleRequest request) {
        return dynamicRuleService.createRule(request);
    }

    @GetMapping
    public DynamicRuleListResponse getAllRules() {
        return dynamicRuleService.getAllRules();
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRule(@PathVariable String productId) {
        dynamicRuleService.deleteRuleByProductId(productId);
    }
}