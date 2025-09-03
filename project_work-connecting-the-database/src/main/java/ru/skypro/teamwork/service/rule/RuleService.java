package ru.skypro.teamwork.service.rule;

import java.util.UUID;

public interface RuleService {

    boolean applyRule(UUID userId);
}