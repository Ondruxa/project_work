package ru.skypro.teamwork.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RuleConditionDto {

    private String query;
    private List<String> arguments;
    private boolean negate;
}