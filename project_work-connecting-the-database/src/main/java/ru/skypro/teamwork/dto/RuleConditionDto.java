package ru.skypro.teamwork.dto;

import lombok.Data;
import java.util.List;

@Data
public class RuleConditionDto {
    private String query;
    private List<String> arguments;
    private boolean negate;
}
