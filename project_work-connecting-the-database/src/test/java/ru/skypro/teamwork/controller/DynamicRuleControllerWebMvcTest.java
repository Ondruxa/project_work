package ru.skypro.teamwork.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.skypro.teamwork.dto.DynamicRuleDto;
import ru.skypro.teamwork.dto.DynamicRuleListResponse;
import ru.skypro.teamwork.dto.DynamicRuleRequest;
import ru.skypro.teamwork.dto.RuleConditionDto;
import ru.skypro.teamwork.service.DynamicRuleService;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DynamicRuleController.class)
public class DynamicRuleControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DynamicRuleService dynamicRuleService;

    @Test
    void createRule_ShouldReturnCreatedRuleAndStatusOk() throws Exception {
        // Given
        String productId = UUID.randomUUID().toString();

        DynamicRuleRequest request = new DynamicRuleRequest();
        request.setProductId(productId); // String
        request.setProductName("Test Product");
        request.setProductText("Test description");

        // Добавляем условия
        RuleConditionDto condition = new RuleConditionDto();
        condition.setQuery("category = ?");
        condition.setArguments(List.of("electronics"));
        condition.setNegate(false);
        request.setRule(List.of(condition));

        DynamicRuleDto responseDto = new DynamicRuleDto();
        responseDto.setId(UUID.randomUUID());
        responseDto.setProductId(UUID.fromString(productId)); // Конвертируем обратно в UUID для DTO
        responseDto.setProductName(request.getProductName());
        responseDto.setProductText(request.getProductText());
        responseDto.setRule(request.getRule());

        when(dynamicRuleService.createRule(any(DynamicRuleRequest.class))).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(post("/rule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.product_id").exists())
                .andExpect(jsonPath("$.product_name").value("Test Product"))
                .andExpect(jsonPath("$.product_text").value("Test description"))
                .andExpect(jsonPath("$.rule").isArray())
                .andExpect(jsonPath("$.rule[0].query").value("category = ?"))
                .andExpect(jsonPath("$.rule[0].arguments[0]").value("electronics"))
                .andExpect(jsonPath("$.rule[0].negate").value(false));

        verify(dynamicRuleService, times(1)).createRule(any(DynamicRuleRequest.class));
    }

    @Test
    void getAllRules_ShouldReturnRulesListAndStatusOk() throws Exception {
        // Given
        DynamicRuleDto rule = new DynamicRuleDto();
        rule.setId(UUID.randomUUID());
        rule.setProductId(UUID.randomUUID());
        rule.setProductName("Test Product");
        rule.setProductText("Test description");

        // Добавляем условия
        RuleConditionDto condition = new RuleConditionDto();
        condition.setQuery("price > ?");
        condition.setArguments(List.of("500"));
        condition.setNegate(true);
        rule.setRule(List.of(condition));

        DynamicRuleListResponse response = new DynamicRuleListResponse();
        response.setData(List.of(rule));

        when(dynamicRuleService.getAllRules()).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/rule"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").exists())
                .andExpect(jsonPath("$.data[0].product_id").exists())
                .andExpect(jsonPath("$.data[0].product_name").value("Test Product"))
                .andExpect(jsonPath("$.data[0].rule[0].query").value("price > ?"))
                .andExpect(jsonPath("$.data[0].rule[0].arguments[0]").value("500"))
                .andExpect(jsonPath("$.data[0].rule[0].negate").value(true));

        verify(dynamicRuleService, times(1)).getAllRules();
    }

    @Test
    void getAllRules_WhenNoRulesExist_ShouldReturnEmptyList() throws Exception {
        // Given
        DynamicRuleListResponse response = new DynamicRuleListResponse();
        response.setData(List.of());

        when(dynamicRuleService.getAllRules()).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/rule"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());

        verify(dynamicRuleService, times(1)).getAllRules();
    }

    @Test
    void deleteRule_ShouldReturnNoContentStatus() throws Exception {
        // Given
        String productId = UUID.randomUUID().toString();

        doNothing().when(dynamicRuleService).deleteRuleByProductId(productId);

        // When & Then
        mockMvc.perform(delete("/rule/{productId}", productId))
                .andExpect(status().isNoContent());

        verify(dynamicRuleService, times(1)).deleteRuleByProductId(productId);
    }

    @Test
    void deleteRule_WithInvalidUuid_ShouldCallService() throws Exception {
        // Given
        String invalidUuid = "invalid-uuid";

        doNothing().when(dynamicRuleService).deleteRuleByProductId(invalidUuid);

        // When & Then
        mockMvc.perform(delete("/rule/{productId}", invalidUuid))
                .andExpect(status().isNoContent());

        verify(dynamicRuleService, times(1)).deleteRuleByProductId(invalidUuid);
    }
}
