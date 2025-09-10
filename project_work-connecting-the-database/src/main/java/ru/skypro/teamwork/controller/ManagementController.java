package ru.skypro.teamwork.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.skypro.teamwork.repository.RecommendationsRepository;

import java.util.Map;

/**
 * Контроллер операций управления сервисом.
 *
 * <ul>
 *   <li>/management/clear-caches — очистка кешей рекомендаций.</li>
 *   <li>/management/info — информация о сборке (имя и версия приложения).</li>
 * </ul>
 */
@RestController
@RequiredArgsConstructor
public class ManagementController {

    private final RecommendationsRepository recommendationsRepository;
    private final BuildProperties buildProperties;

    /**
     * Очищает кеши в {@link RecommendationsRepository}.
     *
     * @return HTTP 204 (NO CONTENT) при успешной очистке
     */
    @PostMapping("/management/clear-caches")
    public ResponseEntity<Void> clearCaches() {
        recommendationsRepository.clearCaches();
        return ResponseEntity.noContent().build();
    }

    /**
     * Возвращает минимальную информацию о сервисе для панели управления.
     *
     * @return карта с полями name и version
     */
    @GetMapping("/management/info")
    public Map<String, String> info() {
        return Map.of(
                "name", buildProperties.getName(),
                "version", buildProperties.getVersion()
        );
    }
}
