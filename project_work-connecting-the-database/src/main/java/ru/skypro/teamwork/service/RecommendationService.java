package ru.skypro.teamwork.service;

import ru.skypro.teamwork.dto.RecommendationDto;
import ru.skypro.teamwork.dto.RecommendationListDto;
import ru.skypro.teamwork.service.ruleset.RecommendationRuleSet;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final List<RecommendationRuleSet> ruleSets;

    public RecommendationService(List<RecommendationRuleSet> ruleSets) {
        this.ruleSets = ruleSets;
    }

    public RecommendationListDto getRecommendations(UUID userId) {
        List<RecommendationDto> recommendations = ruleSets.stream()
                .map(ruleSet -> ruleSet.applyRule(userId))   // Optional<RecommendationDto>
                .flatMap(Optional::stream)               // фильтруем пустые
                .collect(Collectors.toList());

        return new RecommendationListDto(userId, recommendations);
    }
}