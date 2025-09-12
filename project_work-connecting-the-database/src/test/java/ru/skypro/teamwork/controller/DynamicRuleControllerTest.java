package ru.skypro.teamwork.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.skypro.teamwork.dto.DynamicRuleDto;
import ru.skypro.teamwork.dto.DynamicRuleListResponse;
import ru.skypro.teamwork.dto.DynamicRuleRequest;
import ru.skypro.teamwork.dto.RuleConditionDto;
import ru.skypro.teamwork.service.DynamicRuleService;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DynamicRuleControllerTest {

    @Mock
    private DynamicRuleService dynamicRuleService;

    @InjectMocks
    private DynamicRuleController dynamicRuleController;

    @Test
    void createRule_ShouldReturnCreatedRule() {
        // Given
        DynamicRuleRequest request = createTestRequest();
        DynamicRuleDto expectedDto = createTestDto();

        when(dynamicRuleService.createRule(request)).thenReturn(expectedDto);

        // When
        DynamicRuleDto result = dynamicRuleController.createRule(request);

        // Then
        assertNotNull(result);
        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(expectedDto.getProductId(), result.getProductId());
        assertEquals(expectedDto.getProductName(), result.getProductName());
        assertEquals(expectedDto.getProductText(), result.getProductText());
        assertEquals(2, result.getRule().size());

        // Проверяем условия правила
        RuleConditionDto condition1 = result.getRule().get(0);
        assertEquals("category = ?", condition1.getQuery());
        assertEquals(List.of("electronics"), condition1.getArguments());
        assertFalse(condition1.isNegate());

        RuleConditionDto condition2 = result.getRule().get(1);
        assertEquals("price < ?", condition2.getQuery());
        assertEquals(List.of("1000"), condition2.getArguments());
        assertTrue(condition2.isNegate());

        verify(dynamicRuleService, times(1)).createRule(request);
    }

    @Test
    void getAllRules_ShouldReturnListOfRules() {
        // Given
        DynamicRuleDto rule1 = createTestDto();
        DynamicRuleDto rule2 = createTestDto();
        rule2.setId(UUID.randomUUID());
        rule2.setProductName("Another Product");

        DynamicRuleListResponse expectedResponse = new DynamicRuleListResponse();
        expectedResponse.setData(List.of(rule1, rule2));

        when(dynamicRuleService.getAllRules()).thenReturn(expectedResponse);

        // When
        DynamicRuleListResponse result = dynamicRuleController.getAllRules();

        // Then
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(2, result.getData().size());
        assertEquals("Test Product", result.getData().get(0).getProductName());
        assertEquals("Another Product", result.getData().get(1).getProductName());

        // Проверяем условия в первом правиле
        assertEquals(2, result.getData().get(0).getRule().size());

        verify(dynamicRuleService, times(1)).getAllRules();
    }

    @Test
    void getAllRules_WhenNoRulesExist_ShouldReturnEmptyList() {
        // Given
        DynamicRuleListResponse expectedResponse = new DynamicRuleListResponse();
        expectedResponse.setData(List.of());

        when(dynamicRuleService.getAllRules()).thenReturn(expectedResponse);

        // When
        DynamicRuleListResponse result = dynamicRuleController.getAllRules();

        // Then
        assertNotNull(result);
        assertNotNull(result.getData());
        assertTrue(result.getData().isEmpty());

        verify(dynamicRuleService, times(1)).getAllRules();
    }

    @Test
    void deleteRule_WithValidProductId_ShouldCallService() {
        // Given
        String productId = UUID.randomUUID().toString();

        doNothing().when(dynamicRuleService).deleteRuleByProductId(productId);

        // When
        dynamicRuleController.deleteRule(productId);

        // Then
        verify(dynamicRuleService, times(1)).deleteRuleByProductId(productId);
    }

    @Test
    void deleteRule_WhenServiceThrowsException_ShouldPropagateException() {
        // Given
        String productId = UUID.randomUUID().toString();

        doThrow(new RuntimeException("Rule not found"))
                .when(dynamicRuleService).deleteRuleByProductId(productId);

        // When & Then
        assertThrows(RuntimeException.class,
                () -> dynamicRuleController.deleteRule(productId));

        verify(dynamicRuleService, times(1)).deleteRuleByProductId(productId);
    }

    // Вспомогательные методы для создания тестовых объектов
    private DynamicRuleRequest createTestRequest() {
        DynamicRuleRequest request = new DynamicRuleRequest();
        request.setProductId(UUID.randomUUID().toString());
        request.setProductName("Test Product");
        request.setProductText("Test product description");

        return request;
    }

    private DynamicRuleDto createTestDto() {
        DynamicRuleDto dto = new DynamicRuleDto();
        dto.setId(UUID.randomUUID());
        dto.setProductId(UUID.randomUUID());
        dto.setProductName("Test Product");
        dto.setProductText("Test product description");

        // Создаем тестовые условия правила
        RuleConditionDto condition1 = new RuleConditionDto();
        condition1.setQuery("category = ?");
        condition1.setArguments(List.of("electronics"));
        condition1.setNegate(false);

        RuleConditionDto condition2 = new RuleConditionDto();
        condition2.setQuery("price < ?");
        condition2.setArguments(List.of("1000"));
        condition2.setNegate(true);

        dto.setRule(List.of(condition1, condition2));

        return dto;
    }
}
