package ru.skypro.teamwork.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.skypro.teamwork.dto.RecommendationDto;
import ru.skypro.teamwork.dto.RecommendationListDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationTelegramBotServiceTest {

    @Mock
    private TelegramBot telegramBot;

    @Mock
    private RecommendationService recommendationService;

    @Mock
    private UserLookupService userLookupService;

    @InjectMocks
    private RecommendationTelegramBotService botService;

    private final Long CHAT_ID = 12345L;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(botService, "botToken", "test-token");
        ReflectionTestUtils.setField(botService, "bot", telegramBot);
    }

    @Test
    void shutdown_WhenBotIsNotNull_ShouldRemoveListener() {
        // Act
        botService.shutdown();

        // Assert
        verify(telegramBot).removeGetUpdatesListener();
    }

    @Test
    void shutdown_WhenBotIsNull_ShouldNotThrowException() {
        // Arrange
        ReflectionTestUtils.setField(botService, "bot", null);

        // Act & Assert
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> botService.shutdown());
    }

    // Тестируем обработку обновлений через reflection
    @Test
    void testHandleUpdate_WhenStartCommand_ShouldSendWelcomeMessage() throws Exception {
        // Arrange
        Update update = createUpdateWithText("/start");

        // Act - вызываем приватный метод через reflection
        var method = RecommendationTelegramBotService.class.getDeclaredMethod("handleUpdate", Update.class);
        method.setAccessible(true);
        method.invoke(botService, update);

        // Assert
        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sendMessage = captor.getValue();
        assertThat(sendMessage.getParameters().get("chat_id")).isEqualTo(CHAT_ID);
        String text = (String) sendMessage.getParameters().get("text");
        assertThat(text).contains("Добро пожаловать", "/recommend");
    }

    @Test
    void testHandleUpdate_WhenUpdateIsNull_ShouldDoNothing() throws Exception {
        // Act
        var method = RecommendationTelegramBotService.class.getDeclaredMethod("handleUpdate", Update.class);
        method.setAccessible(true);
        method.invoke(botService, new Object[]{null});

        // Assert
        verifyNoInteractions(telegramBot, recommendationService, userLookupService);
    }

    @Test
    void testHandleUpdate_WhenMessageIsNull_ShouldDoNothing() throws Exception {
        // Arrange
        Update update = mock(Update.class);
        when(update.message()).thenReturn(null);

        // Act
        var method = RecommendationTelegramBotService.class.getDeclaredMethod("handleUpdate", Update.class);
        method.setAccessible(true);
        method.invoke(botService, update);

        // Assert
        verifyNoInteractions(telegramBot, recommendationService, userLookupService);
    }

    @Test
    void testHandleUpdate_WhenTextIsNull_ShouldDoNothing() throws Exception {
        // Arrange
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn(null);

        // Act
        var method = RecommendationTelegramBotService.class.getDeclaredMethod("handleUpdate", Update.class);
        method.setAccessible(true);
        method.invoke(botService, update);

        // Assert
        verifyNoInteractions(telegramBot, recommendationService, userLookupService);
    }

    @Test
    void testHandleUpdate_WhenRecommendCommandWithoutUsername_ShouldSendUsageMessage() throws Exception {
        // Arrange
        Update update = createUpdateWithText("/recommend");

        // Act
        var method = RecommendationTelegramBotService.class.getDeclaredMethod("handleUpdate", Update.class);
        method.setAccessible(true);
        method.invoke(botService, update);

        // Assert
        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sendMessage = captor.getValue();
        assertThat(sendMessage.getParameters().get("chat_id")).isEqualTo(CHAT_ID);
        String text = (String) sendMessage.getParameters().get("text");
        assertThat(text).isEqualTo("Использование: /recommend <username>");
    }

    @Test
    void testHandleUpdate_WhenUserNotFound_ShouldSendUserNotFoundMessage() throws Exception {
        // Arrange
        Update update = createUpdateWithText("/recommend nonexistent");
        when(userLookupService.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act
        var method = RecommendationTelegramBotService.class.getDeclaredMethod("handleUpdate", Update.class);
        method.setAccessible(true);
        method.invoke(botService, update);

        // Assert
        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sendMessage = captor.getValue();
        assertThat(sendMessage.getParameters().get("chat_id")).isEqualTo(CHAT_ID);
        String text = (String) sendMessage.getParameters().get("text");
        assertThat(text).isEqualTo("Пользователь не найден");
        verify(userLookupService).findByUsername("nonexistent");
    }

    @Test
    void testHandleUpdate_WhenUserFoundWithNoRecommendations_ShouldSendNoProductsMessage() throws Exception {
        // Arrange
        Update update = createUpdateWithText("/recommend testuser");
        UUID userId = UUID.randomUUID();
        UserLookupService.UserInfo userInfo = new UserLookupService.UserInfo(userId, "John", "Doe");

        when(userLookupService.findByUsername("testuser")).thenReturn(Optional.of(userInfo));

        // Создаем RecommendationListDto с пустым списком рекомендаций
        RecommendationListDto emptyRecommendations = new RecommendationListDto(
                userId, "John", "Doe", List.of()
        );
        when(recommendationService.getRecommendations(userId)).thenReturn(emptyRecommendations);

        // Act
        var method = RecommendationTelegramBotService.class.getDeclaredMethod("handleUpdate", Update.class);
        method.setAccessible(true);
        method.invoke(botService, update);

        // Assert
        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sendMessage = captor.getValue();
        String text = (String) sendMessage.getParameters().get("text");
        assertThat(text).contains("Здравствуйте John Doe");
        assertThat(text).contains("Новых продуктов нет");
        verify(userLookupService).findByUsername("testuser");
        verify(recommendationService).getRecommendations(userId);
    }

    @Test
    void testHandleUpdate_WhenUserFoundWithRecommendations_ShouldSendProductsList() throws Exception {
        // Arrange
        Update update = createUpdateWithText("/recommend testuser");
        UUID userId = UUID.randomUUID();
        UserLookupService.UserInfo userInfo = new UserLookupService.UserInfo(userId, "Jane", "Smith");

        // Генерируем уникальные ID для каждой рекомендации
        UUID product1Id = UUID.randomUUID();
        UUID product2Id = UUID.randomUUID();

        RecommendationDto rec1 = new RecommendationDto(product1Id, "Product 1", "Description 1");
        RecommendationDto rec2 = new RecommendationDto(product2Id, "Product 2", null);

        // Создаем RecommendationListDto с рекомендациями
        RecommendationListDto recommendations = new RecommendationListDto(
                userId, "Jane", "Smith", List.of(rec1, rec2)
        );

        when(userLookupService.findByUsername("testuser")).thenReturn(Optional.of(userInfo));
        when(recommendationService.getRecommendations(userId)).thenReturn(recommendations);

        // Act
        var method = RecommendationTelegramBotService.class.getDeclaredMethod("handleUpdate", Update.class);
        method.setAccessible(true);
        method.invoke(botService, update);

        // Assert
        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sendMessage = captor.getValue();
        String text = (String) sendMessage.getParameters().get("text");
        assertThat(text).contains("Здравствуйте Jane Smith");
        assertThat(text).contains("Новые продукты для вас");
        assertThat(text).contains("1) Product 1");
        assertThat(text).contains("   Description 1");
        assertThat(text).contains("2) Product 2");
        verify(userLookupService).findByUsername("testuser");
        verify(recommendationService).getRecommendations(userId);
    }

    @Test
    void testHandleUpdate_WhenRecommendCommandWithAtSymbol_ShouldTrimAtSymbol() throws Exception {
        // Arrange
        Update update = createUpdateWithText("/recommend @testuser");
        UUID userId = UUID.randomUUID();
        UserLookupService.UserInfo userInfo = new UserLookupService.UserInfo(userId, "Test", "User");

        when(userLookupService.findByUsername("@testuser")).thenReturn(Optional.of(userInfo));

        RecommendationListDto emptyRecommendations = new RecommendationListDto(
                userId, "Test", "User", List.of()
        );
        when(recommendationService.getRecommendations(userId)).thenReturn(emptyRecommendations);

        // Act
        var method = RecommendationTelegramBotService.class.getDeclaredMethod("handleUpdate", Update.class);
        method.setAccessible(true);
        method.invoke(botService, update);

        // Assert
        verify(userLookupService).findByUsername("@testuser");
    }

    // Тестируем sendSafe через reflection
    @Test
    void testSendSafe_ShouldSendMessage() throws Exception {
        // Act
        var method = RecommendationTelegramBotService.class.getDeclaredMethod("sendSafe", Long.class, String.class);
        method.setAccessible(true);
        method.invoke(botService, CHAT_ID, "test message");

        // Assert
        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sendMessage = captor.getValue();
        assertThat(sendMessage.getParameters().get("chat_id")).isEqualTo(CHAT_ID);
        assertThat(sendMessage.getParameters().get("text")).isEqualTo("test message");
    }

    @Test
    void testSendSafe_WhenTelegramBotThrowsException_ShouldNotPropagate() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Telegram API error")).when(telegramBot).execute(any(SendMessage.class));

        // Act & Assert
        var method = RecommendationTelegramBotService.class.getDeclaredMethod("sendSafe", Long.class, String.class);
        method.setAccessible(true);
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() ->
                method.invoke(botService, CHAT_ID, "test message")
        );
    }

    private Update createUpdateWithText(String text) {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);

        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn(text);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(CHAT_ID);

        return update;
    }
}
