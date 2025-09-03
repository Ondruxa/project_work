package ru.skypro.teamwork.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.teamwork.dto.RecommendationListDto;
import ru.skypro.teamwork.service.RecommendationService;
import ru.skypro.teamwork.service.UserLookupService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/recommendation")
@RequiredArgsConstructor
public class RecommendationsController {

    private final RecommendationService recommendationService;
    private final UserLookupService userLookupService;

    @GetMapping("/userid/{userId}")
    public ResponseEntity<RecommendationListDto> getRecommendationsByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(recommendationService.getRecommendations(userId));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<RecommendationListDto> getRecommendationsByUsername(@PathVariable String username) {
        return userLookupService.findByUsername(username)
                .map(userInfo -> ResponseEntity.ok(recommendationService.getRecommendations(userInfo.id())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}