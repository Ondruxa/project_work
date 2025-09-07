package ru.skypro.teamwork.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.skypro.teamwork.dto.RecommendationDto;
import ru.skypro.teamwork.dto.RecommendationListDto;
import ru.skypro.teamwork.model.DynamicRule;
import ru.skypro.teamwork.model.RuleCondition;
import ru.skypro.teamwork.model.RuleConditionArgument;
import ru.skypro.teamwork.repository.DynamicRuleRepository;
import ru.skypro.teamwork.repository.RecommendationsRepository;
import ru.skypro.teamwork.service.ruleset.RecommendationRuleSetService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

public class RecommendationServiceTest {

    @Mock
    private RecommendationRuleSetService ruleSet1;
    @Mock
    private RecommendationRuleSetService ruleSet2;
    @Mock
    private DynamicRuleRepository dynamicRuleRepository;
    @Mock
    private RecommendationsRepository recommendationsRepository;
    @Mock
    private UserLookupService userLookupService; // новый зависимый сервис
    @Mock
    private RuleStatsService ruleStatsService;   // новый зависимый сервис

    private RecommendationService recommendationService;


    @BeforeEach
    void setUp() {
        recommendationService = new RecommendationService(
                List.of(ruleSet1, ruleSet2),
                dynamicRuleRepository,
                recommendationsRepository,
                userLookupService,
                ruleStatsService);
    }

    @Test
    void getRecommendations_ShouldReturnEmptyList_WhenNoRulesMatch() {
        UUID userId = UUID.randomUUID(); // Создаем случайный ID пользователя для теста

        when(ruleSet1.applyRule(userId)).thenReturn(Optional.empty());
        when(ruleSet2.applyRule(userId)).thenReturn(Optional.empty());
        when(dynamicRuleRepository.findAll()).thenReturn(Collections.emptyList());

        RecommendationListDto result = recommendationService.getRecommendations(userId);

        //проверки:
        assertEquals(userId, result.getUserId()); //userId в DTO совпадает с переданным
        assertTrue(result.getRecommendations().isEmpty()); //список рекомендаций пустой

        verify(ruleSet1).applyRule(userId);
        verify(ruleSet2).applyRule(userId);
        verify(dynamicRuleRepository).findAll();

    }

    @Test
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
    void getRecommendations_ShouldHandleEmptyRuleSets() {
        RecommendationService emptyService = new RecommendationService(Collections.emptyList(),
                dynamicRuleRepository,
                recommendationsRepository,
                userLookupService,
                ruleStatsService);
        UUID userId = UUID.randomUUID();
        when(dynamicRuleRepository.findAll()).thenReturn(Collections.emptyList());

        RecommendationListDto result = emptyService.getRecommendations(userId);

        assertAll(
                () -> assertEquals(userId, result.getUserId(), "User ID должен совпадать"),
                () -> assertTrue(result.getRecommendations().isEmpty(), "Список должен быть пустым")
        );
    }

    @Test
    void getRecommendations_ShouldIncludeDynamicRules_WhenConditionsMet() {
        UUID userId = UUID.randomUUID();
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();
        UUID dynamicProductId = UUID.randomUUID();

        RecommendationDto recommendation1 = new RecommendationDto(productId1, "Кредитная карта", "Рекомендации1");
        RecommendationDto recommendation2 = new RecommendationDto(productId2, "Дебетовая карта", "Рекомендации2");

        when(ruleSet1.applyRule(userId)).thenReturn(Optional.of(recommendation1));
        when(ruleSet2.applyRule(userId)).thenReturn(Optional.of(recommendation2));

        DynamicRule dynamicRule = new DynamicRule();
        dynamicRule.setProductId(dynamicProductId); // исправлено
        dynamicRule.setProductName("Динамический продукт");
        dynamicRule.setProductText("Описание динамического продукта");

        // Создаем условие
        RuleCondition condition = new RuleCondition();
        condition.setQuery("USER_OF");
        condition.setNegate(false);

        // Создаем аргументы
        RuleConditionArgument argument = new RuleConditionArgument();
        argument.setArgument("Кредитная карта");
        condition.setArguments(List.of(argument));

        dynamicRule.setRule(List.of(condition));

        // Мокируем репозиторий динамических правил
        when(dynamicRuleRepository.findAll()).thenReturn(List.of(dynamicRule));

        // Мокируем проверку условия - возвращаем true (условие выполнено)
        when(recommendationsRepository.userHasProductType(eq(userId), eq("Кредитная карта"))).thenReturn(true);

        RecommendationListDto result = recommendationService.getRecommendations(userId);
        assertAll(
                () -> assertEquals(3, result.getRecommendations().size(), "Необходимо 3 рекомендации"),
                () -> assertTrue(result.getRecommendations().stream()
                                .anyMatch(dto -> dto.getProductId().equals(dynamicProductId)),
                        "Рекомендации из динамического правила")
        );
    }

    @Test
    void getRecommendations_ShouldNotIncludeDynamicRules_WhenConditionsNotMet() {
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        RecommendationDto recommendation = new RecommendationDto(productId, "Кредитная карта", "Рекомендации");
        when(ruleSet1.applyRule(userId)).thenReturn(Optional.of(recommendation));
        when(ruleSet2.applyRule(userId)).thenReturn(Optional.empty());

        DynamicRule dynamicRule = new DynamicRule();
        dynamicRule.setProductId(UUID.randomUUID()); // исправлено
        dynamicRule.setProductName("Динамический продукт");
        dynamicRule.setProductText("Рекомендации к динамическому продукту");

        RuleCondition condition = new RuleCondition();
        condition.setQuery("USER_OF");
        condition.setNegate(false);

        RuleConditionArgument argument = new RuleConditionArgument();
        argument.setArgument("СБЕРЕГАТЕЛЬНЫЙ СЧЕТ");
        condition.setArguments(List.of(argument));

        dynamicRule.setRule(List.of(condition));

        when(dynamicRuleRepository.findAll()).thenReturn(List.of(dynamicRule));
        when(recommendationsRepository.userHasProductType(eq(userId), eq("СБЕРЕГАТЕЛЬНЫЙ СЧЕТ"))).thenReturn(false);

        RecommendationListDto result = recommendationService.getRecommendations(userId);

        assertAll(
                () -> assertEquals(1, result.getRecommendations().size(), "Необходима 1 рекомендация"),
                () -> assertEquals(productId, result.getRecommendations().get(0).getProductId(),
                        "Рекомендации должны быть из ruleSet")
        );
    }

    @Test
    void getRecommendations_ShouldHandleMultipleConditions() {
        UUID userId = UUID.randomUUID();
        UUID dynamicProductId = UUID.randomUUID();

        when(ruleSet1.applyRule(userId)).thenReturn(Optional.empty());
        when(ruleSet2.applyRule(userId)).thenReturn(Optional.empty());

        DynamicRule dynamicRule = new DynamicRule();
        dynamicRule.setProductId(dynamicProductId); // исправлено
        dynamicRule.setProductName("Универсальный продукт");
        dynamicRule.setProductText("Описание универсального продукта");

        // Первое условие
        RuleCondition condition1 = new RuleCondition();
        condition1.setQuery("USER_OF");
        condition1.setNegate(false);
        RuleConditionArgument argument1 = new RuleConditionArgument();
        argument1.setArgument("КРЕДИТНАЯ КАРТА");
        condition1.setArguments(List.of(argument1));

        // Второе условие
        RuleCondition condition2 = new RuleCondition();
        condition2.setQuery("ACTIVE_USER_OF");
        condition2.setNegate(false);
        RuleConditionArgument argument2 = new RuleConditionArgument();
        argument2.setArgument("ДЕБЕТОВАЯ КАРТА");
        condition2.setArguments(List.of(argument2));

        dynamicRule.setRule(List.of(condition1, condition2));

        when(dynamicRuleRepository.findAll()).thenReturn(List.of(dynamicRule));

        when(recommendationsRepository.userHasProductType(eq(userId), eq("КРЕДИТНАЯ КАРТА"))).thenReturn(true);
        when(recommendationsRepository.userHasActiveProductType(eq(userId), eq("ДЕБЕТОВАЯ КАРТА"), eq(5))).thenReturn(true);

        RecommendationListDto result = recommendationService.getRecommendations(userId);

        assertAll(
                () -> assertEquals(1, result.getRecommendations().size(), "Необходима 1 рекомендация"),
                () -> assertEquals(dynamicProductId, result.getRecommendations().get(0).getProductId(),
                        "Рекомендации должны быть из динамического правила")
        );


    }
}
