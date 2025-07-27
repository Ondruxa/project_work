package ru.skypro.teamwork.controller;

import org.springframework.web.bind.annotation.*;
import ru.skypro.teamwork.dto.RecommendationListDto;
import ru.skypro.teamwork.service.RecommendationService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/recommendation")
public class RecommendationsController {
    private final RecommendationService recommendationService;

    public RecommendationsController(RecommendationService recommendationsService) {
        this.recommendationService = recommendationsService;
    }

    @GetMapping("/{userId}")
    public RecommendationListDto getRecommendations(@PathVariable UUID userId) {
        return recommendationService.getRecommendations(userId);
    }
}