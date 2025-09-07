package ru.skypro.teamwork.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.skypro.teamwork.dto.RecommendationDto;
import ru.skypro.teamwork.dto.RecommendationListDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationTelegramBotService {

    @Value("${telegram.bot.token}")
    private String botToken;

    private final RecommendationService recommendationService;
    private final UserLookupService userLookupService;

    private TelegramBot bot;

    @PostConstruct
    public void init() {
        bot = new TelegramBot(botToken);
        bot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                try {
                    handleUpdate(update);
                } catch (Exception e) {
                    log.error("Ошибка обработки апдейта", e);
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, e -> log.error("Ошибка получения апдейтов: {}", e.getMessage(), e));
        log.info("Telegram bot started");
    }

    @PreDestroy
    public void shutdown() {
        if (bot != null) {
            bot.removeGetUpdatesListener();
            log.info("Telegram bot stopped");
        }
    }

    private void handleUpdate(Update update) {
        if (update == null) return;
        Message msg = update.message();
        if (msg == null || msg.text() == null) return;

        String text = msg.text().trim();
        if (text.startsWith("/start")) {
            String help = "Добро пожаловать!\n\n" +
                    "Доступные команды:\n" +
                    "/recommend <username> — получить рекомендации для пользователя.";
            sendSafe(msg.chat().id(), help);
            return;
        }

        if (!text.startsWith("/recommend")) return;

        String[] parts = text.split("\\s+", 2);
        if (parts.length < 2 || parts[1].isBlank()) {
            sendSafe(msg.chat().id(), "Использование: /recommend <username>");
            return;
        }
        String username = parts[1].trim();

        userLookupService.findByUsername(username)
                .ifPresentOrElse(userInfo -> {
                    RecommendationListDto list = recommendationService.getRecommendations(userInfo.id());
                    StringBuilder sb = new StringBuilder();
                    sb.append("Здравствуйте ")
                            .append(userInfo.firstName()).append(" ")
                            .append(userInfo.lastName()).append("\n\n");
                    if (list.getRecommendations() == null || list.getRecommendations().isEmpty()) {
                        sb.append("Новых продуктов нет.");
                    } else {
                        sb.append("Новые продукты для вас:\n");
                        int i = 1;
                        for (RecommendationDto r : list.getRecommendations()) {
                            sb.append(i++).append(") ").append(r.getTitle()).append("\n");
                            if (r.getDescription() != null && !r.getDescription().isBlank()) {
                                sb.append("   ").append(r.getDescription()).append("\n");
                            }
                        }
                    }
                    sendSafe(msg.chat().id(), sb.toString());
                }, () -> sendSafe(msg.chat().id(), "Пользователь не найден"));
    }

    private void sendSafe(Long chatId, String text) {
        try {
            bot.execute(new SendMessage(chatId, text));
        } catch (Exception e) {
            log.error("Не удалось отправить сообщение: {}", e.getMessage(), e);
        }
    }
}
