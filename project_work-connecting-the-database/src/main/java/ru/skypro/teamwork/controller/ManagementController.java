package ru.skypro.teamwork.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.skypro.teamwork.repository.RecommendationsRepository;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ManagementController {

    private final RecommendationsRepository recommendationsRepository;
    private final BuildProperties buildProperties;

    @PostMapping("/management/clear-caches")
    public ResponseEntity<Void> clearCaches() {
        recommendationsRepository.clearCaches();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/management/info")
    public Map<String, String> info() {
        // Возвращаем только требуемые поля
        return Map.of(
                "name", buildProperties.getName(),
                "version", buildProperties.getVersion()
        );
    }
}
