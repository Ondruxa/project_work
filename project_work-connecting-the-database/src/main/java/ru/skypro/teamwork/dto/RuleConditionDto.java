package ru.skypro.teamwork.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO одного условия динамического правила.
 * <p>
 * Содержит шаблон SQL/логического запроса (query), список аргументов (arguments),
 * а также флаг отрицания (negate), который указывает, что результат условия
 * должен быть инвертирован при вычислении.
 */
@Getter
@Setter
public class RuleConditionDto {

    private String query;
    private List<String> arguments;
    private boolean negate;
}