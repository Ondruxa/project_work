package ru.skypro.teamwork.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.skypro.teamwork.repository.sql.UserSql;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(@Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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

    public record UserRecord(UUID id, String firstName, String lastName) {
        public String fullName() { return firstName + " " + lastName; }
    }
}