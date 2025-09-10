package ru.skypro.teamwork.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Обёртка для списка динамических правил.
 * Используется для единообразного формата ответа REST API.
 */
@Getter
@Setter
public class DynamicRuleListResponse {

    private List<DynamicRuleDto> data;
}