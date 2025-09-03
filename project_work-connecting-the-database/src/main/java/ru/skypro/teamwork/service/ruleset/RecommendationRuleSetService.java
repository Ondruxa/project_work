package ru.skypro.teamwork.service.ruleset;

import ru.skypro.teamwork.dto.RecommendationDto;

import java.util.Optional;
import java.util.UUID;

public interface RecommendationRuleSetService {

    Optional<RecommendationDto> applyRule(UUID userId);
}
