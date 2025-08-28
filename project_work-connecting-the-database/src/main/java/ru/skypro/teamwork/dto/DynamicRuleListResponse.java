package ru.skypro.teamwork.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DynamicRuleListResponse {
    private List<DynamicRuleDto> data;
}