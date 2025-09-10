package ru.skypro.teamwork.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.skypro.teamwork.repository.sql.UserSql;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий пользователей (read-only)
 * Использует {@link JdbcTemplate} для выборок пользователя по username или id.
 */
@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * @param jdbcTemplate специализированный JdbcTemplate для источника рекомендаций
     */
    public UserRepository(@Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Ищет пользователя по username (допускается передача с символом '@').
     * Возвращает Optional пустой, если входной параметр некорректен или не найден.
     *
     * @param rawUsername исходная строка username
     * @return Optional с записью пользователя
     */
    public Optional<UserRecord> findByUsername(String rawUsername) {
        if (rawUsername == null || rawUsername.isBlank()) return Optional.empty();
        String username = rawUsername.trim().replaceAll("^@", "");
        List<UserRecord> list = jdbcTemplate.query(UserSql.FIND_BY_USERNAME,
                (rs, i) -> new UserRecord(
                        rs.getObject("id", UUID.class),
                        rs.getString("first_name"),
                        rs.getString("last_name")
                ),
                username);
        return list.size() == 1 ? Optional.of(list.getFirst()) : Optional.empty();
    }

    /**
     * Ищет пользователя по UUID.
     * @param userId идентификатор пользователя
     * @return Optional с записью пользователя если найден
     */
    public Optional<UserRecord> findById(UUID userId) {
        List<UserRecord> list = jdbcTemplate.query(
                UserSql.FIND_BY_ID,
                (rs, i) -> new UserRecord(
                        rs.getObject("id", UUID.class),
                        rs.getString("first_name"),
                        rs.getString("last_name")
                ),
                userId
        );
        return list.size() == 1 ? Optional.of(list.getFirst()) : Optional.empty();
    }

    /**
     * Иммутабельная запись пользователя.
     * @param id UUID пользователя
     * @param firstName имя
     * @param lastName фамилия
     */
    public record UserRecord(UUID id, String firstName, String lastName) {
        /**
         * Полное имя (имя + фамилия).
         * @return строка полного имени
         */
        public String fullName() { return firstName + " " + lastName; }
    }
}