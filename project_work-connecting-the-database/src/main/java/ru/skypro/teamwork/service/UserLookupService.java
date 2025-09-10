package ru.skypro.teamwork.service;

import org.springframework.stereotype.Service;
import ru.skypro.teamwork.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Сервис поиска пользователей для получения базовой информации (id, имя, фамилия).
 * Используется ботовыми и рекомендательными компонентами.
 */
@Service
public class UserLookupService {

    private final UserRepository userRepository;

    public UserLookupService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Ищет пользователя по username (регистрозависимость определяется реализацией репозитория).
     * @param usernameRaw логин/username
     * @return Optional с UserInfo если найден
     */
    public Optional<UserInfo> findByUsername(String usernameRaw) {
        return userRepository.findByUsername(usernameRaw)
                .map(r -> new UserInfo(r.id(), r.firstName(), r.lastName()));
    }

    /**
     * DTO с минимальным набором данных о пользователе.
     * Метод fullName возвращает объединённые имя и фамилию.
     * @param id идентификатор пользователя
     * @param firstName имя
     * @param lastName фамилия
     */
    public record UserInfo(UUID id, String firstName, String lastName) {
        public String fullName() {
            return firstName + " " + lastName;
        }
    }

    /**
     * Ищет пользователя по UUID.
     * @param userId идентификатор пользователя
     * @return Optional с UserInfo если найден
     */
    public Optional<UserInfo> findById(UUID userId) {
        return userRepository.findById(userId)
                .map(r -> new UserInfo(r.id(), r.firstName(), r.lastName()));
    }
}