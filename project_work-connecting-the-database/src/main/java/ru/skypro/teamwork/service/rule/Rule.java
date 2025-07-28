package ru.skypro.teamwork.service.rule;

import java.util.UUID;

public interface Rule {
    boolean applyRule(UUID userId);
}