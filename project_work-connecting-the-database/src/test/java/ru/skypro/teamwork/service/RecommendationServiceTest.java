package ru.skypro.teamwork.service;

import org.h2.command.dml.MergeUsing;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.skypro.teamwork.dto.RecommendationDto;
import ru.skypro.teamwork.dto.RecommendationListDto;
import ru.skypro.teamwork.service.ruleset.RecommendationRuleSet;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) //одключение Mockito

public class RecommendationServiceTest {

    //mock-объекты для зависимостей
    @Mock
    private RecommendationRuleSet ruleSet1;
    @Mock
    private RecommendationRuleSet ruleSet2;

    private RecommendationService recommendationService;


    @BeforeEach
    void setUp() {
        //создание списка mock-ов и внедрение его в сервис

        recommendationService = new RecommendationService(List.of(ruleSet1, ruleSet2));
    }

    @Test
    //Поведение сервиса, когда ни одно из правил не возвращает рекомендаций.
    void getRecommendations_ShouldReturnEmptyList_WhenNoRulesMatch() {
        UUID userId = UUID.randomUUID(); // Создаем случайный ID пользователя для теста

        //при вызове applyRule с любым userId возвращаем Optional.empty()
        when(ruleSet1.applyRule(userId)).thenReturn(Optional.empty());
        when(ruleSet2.applyRule(userId)).thenReturn(Optional.empty());

        RecommendationListDto result = recommendationService.getRecommendations(userId);

        //проверки:
        assertEquals(userId, result.getUserId()); //userId в DTO совпадает с переданным
        assertTrue(result.getRecommendations().isEmpty()); //список рекомендаций пустой

    }

    @Test
    //Корректность работы сервиса, когда правила возвращают рекомендации.
    void getRecommendations_ShouldReturnRecommendations_WhenRulesMatch() {
        UUID userId = UUID.randomUUID(); // подготовка тестовых данных
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();

        RecommendationDto recommendation1 = new RecommendationDto(productId1, "Кредитная карта Premium",
                "Оформите кредитную карту с кэшбэком 5%");
        RecommendationDto recommendation2 = new RecommendationDto(productId2, "Дебетовая карта Travel", "Бесплатные снятия по всему миру");

        when(ruleSet1.applyRule(userId)).thenReturn(Optional.of(recommendation1));
        when(ruleSet2.applyRule(userId)).thenReturn(Optional.of(recommendation2));

        RecommendationListDto result = recommendationService.getRecommendations(userId);

        assertAll(
                () -> assertEquals(userId, result.getUserId(), "Id должен совпадать"),
                () -> assertEquals(2, result.getRecommendations().size(), "Необходимо 2 рекомендации"),
                () -> assertTrue(result.getRecommendations().stream()
                                .anyMatch(dto -> dto.getProductId().equals(productId1) &&
                                        dto.getTitle().equals("Кредитная карта Premium")),
                        "Одна рекомендация обязательна"),
                () -> assertTrue(result.getRecommendations().stream()
                                .anyMatch(dto -> dto.getProductId().equals(productId2) &&
                                        dto.getDescription().contains("Бесплатные снятия")),
                        "Две рекомендации обязательны")
        );
    }

    @Test
    //Проверка фильтрации пустых рекомендаций, когда некоторые правила не срабатывают.
    void getRecommendations_ShouldFilterEmptyRecommendations() {
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        RecommendationDto recommendation = new RecommendationDto(productId, "Сберегательный счет", "Накопительный счет");

        when(ruleSet1.applyRule(userId)).thenReturn(Optional.empty());
        when(ruleSet2.applyRule(userId)).thenReturn(Optional.of(recommendation));

        RecommendationListDto result = recommendationService.getRecommendations(userId);

        assertAll(
                () -> assertEquals(1, result.getRecommendations().size(), "Должна быть 1 рекомендация"),
                () -> assertEquals(productId, result.getRecommendations().get(0).getProductId(), "Product Id должен совпадать"),
                () -> assertEquals("Сберегательный счет", result.getRecommendations().get(0).getTitle(), "Название должно совпадать")
        );
    }

    @Test
    //Поведение сервиса при отсутствии правил (пустой список ruleSets).
    void getRecommendations_ShouldHandleEmptyRuleSets() {
        RecommendationService emptyService = new RecommendationService(Collections.emptyList());
        UUID userId = UUID.randomUUID();

        RecommendationListDto result = emptyService.getRecommendations(userId);

        assertAll(
                () -> assertEquals(userId,result.getUserId(), "User ID должен совпадать"),
                () -> assertTrue(result.getRecommendations().isEmpty(), "Список должен быть пустым")
        );
    }
}
