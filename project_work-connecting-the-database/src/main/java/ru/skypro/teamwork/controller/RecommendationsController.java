package ru.skypro.teamwork.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.teamwork.dto.RecommendationListDto;
import ru.skypro.teamwork.service.RecommendationService;
import ru.skypro.teamwork.service.UserLookupService;

import java.util.List;
import java.util.UUID;

/**
 * Контроллер получения рекомендаций для пользователя.
 * <p>
 * Базовый путь: /recommendation
 * <ul>
 *   <li>GET /recommendation/userid/{userId} — рекомендации по UUID пользователя.</li>
 *   <li>GET /recommendation/username/{username} — рекомендации по username (если найден).</li>
 * </ul>
 */
@RestController
@RequestMapping("/recommendation")
@RequiredArgsConstructor
public class RecommendationsController {

    private final RecommendationService recommendationService;
    private final UserLookupService userLookupService;

    /**
     * Возвращает список рекомендаций по UUID пользователя.
     *
     * @param userId идентификатор пользователя
     * @return DTO со списком рекомендаций или 200 с пустым списком, если их нет
     */
    @GetMapping("/userid/{userId}")
    public ResponseEntity<RecommendationListDto> getRecommendationsByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(recommendationService.getRecommendations(userId));
    }

    /**
     * Ищет пользователя по username и возвращает его рекомендации.
     *
     * @param username логин пользователя
     * @return 200 + рекомендации если найден; 404 если пользователь отсутствует
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<RecommendationListDto> getRecommendationsByUsername(@PathVariable String username) {
        return userLookupService.findByUsername(username)
                .map(userInfo -> ResponseEntity.ok(recommendationService.getRecommendations(userInfo.id())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}