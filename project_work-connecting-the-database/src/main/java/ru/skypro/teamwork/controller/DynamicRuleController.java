package ru.skypro.teamwork.controller;

import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<DynamicRuleDto> createRule(@RequestBody DynamicRuleRequest request) {
        DynamicRuleDto created = dynamicRuleService.createRule(request);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<DynamicRuleListResponse> getAllRules() {
        DynamicRuleListResponse response = dynamicRuleService.getAllRules();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteRule(@PathVariable String productId) {
        dynamicRuleService.deleteRuleByProductId(productId);
        return ResponseEntity.noContent().build();
    }
}