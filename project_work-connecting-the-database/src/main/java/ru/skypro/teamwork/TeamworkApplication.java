package ru.skypro.teamwork;

import liquibase.Liquibase;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;

@SpringBootApplication
public class TeamworkApplication {

    public static void main(String[] args) {
        SpringApplication.run(TeamworkApplication.class, args);
    }
}