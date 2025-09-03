package ru.skypro.teamwork.repository.sql;

public final class UserSql {

    private UserSql() {}

    public static final String FIND_BY_USERNAME = """
        SELECT id, first_name, last_name
        FROM users
        WHERE LOWER(username)=LOWER(?)
        """;

    public static final String FIND_BY_ID = """
        SELECT id, first_name, last_name FROM users WHERE id = ?
        """;
}