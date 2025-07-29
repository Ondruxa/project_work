package ru.skypro.teamwork.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.skypro.teamwork.dto.RecommendationDto;
import ru.skypro.teamwork.dto.RecommendationListDto;
import ru.skypro.teamwork.service.RecommendationService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RecommendationsControllerTest {

    // MockMvc - основной инструмент для тестирования Spring MVC контроллеров
    private MockMvc mockMvc;

    @Mock
    private RecommendationService recommendationService;

    @InjectMocks
    private RecommendationsController recommendationsController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        //recommendationsController = new RecommendationsController(recommendationService);
        mockMvc = MockMvcBuilders.standaloneSetup(recommendationsController).build();
    }

    @Test
    //Проверяет, что endpoint возвращает статус 200 OK при валидном запросе.
    void getRecommendations_ShouldReturnRecommendationsList_WhenValidRequest() throws Exception {
        UUID userId = UUID.randomUUID();
        RecommendationDto dto1 = new RecommendationDto(
                UUID.randomUUID(),
                "Кредитная карта",
                "Накопительная карта"
        );
        RecommendationDto dto2 = new RecommendationDto(
                UUID.randomUUID(),
                "Дебетовая карта",
                "Бесплатное снятие"
        );

        when(recommendationService.getRecommendations(userId))
                .thenReturn(new RecommendationListDto(userId, List.of(dto1, dto2)));

        mockMvc.perform(get("/recommendation/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.recommendations.length()").value(2));
    }

    @Test
    //Сервер возвращает статус 200 OK (а не ошибку), даже если рекомендаций нет.
    void getRecommendations_ShouldReturnEmptyList_WhenNoRecommendations() throws Exception {
        UUID userId = UUID.randomUUID();

        Mockito.when(recommendationService.getRecommendations(userId))
                .thenReturn(new RecommendationListDto(userId, List.of()));

        mockMvc.perform(get("/recommendation/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recommendations").isEmpty());
    }

    @Test
    //API возвращает статус 400 Bad Request, если передан:
    // - не-UUID строка
    // - пустой ID
    // - случайные символы
    void getRecommendations_ShouldReturn400_WhenInvalidUserIdFormat() throws Exception {
        mockMvc.perform(get("/recommendation/{userId}", "invalid-uuid-format"))
                .andExpect(status().isBadRequest());
    }


}
