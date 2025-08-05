package ru.skypro.teamwork.dto;

import lombok.Data;
import java.util.List;

@Data
public class DynamicRuleListResponse {
    private List<DynamicRuleDto> data;
}