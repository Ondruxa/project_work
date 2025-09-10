package ru.skypro.teamwork.configuration;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Primary;

/**
 * Конфигурация источников данных.
 * <p>
 * Определяет:
 * <ul>
 *   <li>Основной {@link DataSource} (bean name: defaultDataSource) на основе стандартных spring.datasource.* свойств.</li>
 *   <li>Отдельный read-only H2 {@link DataSource} (bean name: recommendationsDataSource) для предрассчитанных данных рекомендаций.</li>
 *   <li>{@link JdbcTemplate} для каждого источника (recommendationsJdbcTemplate и основной jdbcTemplate).</li>
 * </ul>
 */
@Configuration
public class RecommendationsDataSourceConfiguration {

    /**
     * Создаёт основной DataSource, инициализируемый стандартными свойствами
     *
     * @param properties свойства автоконфигурации источника данных
     * @return основной {@link DataSource}
     */
    @Primary
    @Bean(name = "defaultDataSource")
    public DataSource defaultDataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

    /**
     * Создаёт read-only H2 DataSource для хранения/чтения данных рекомендаций.
     * URL берётся из свойства application.recommendations-db.url.
     *
     * @param recommendationsUrl JDBC URL для H2
     * @return read-only {@link DataSource} H2
     */
    @Bean(name = "recommendationsDataSource")
    public DataSource recommendationsDataSource(@Value("${application.recommendations-db.url}") String recommendationsUrl) {
        var dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(recommendationsUrl);
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setReadOnly(true);
        return dataSource;
    }

    /**
     * {@link JdbcTemplate}, связанный с read-only источником данных рекомендаций.
     *
     * @param dataSource read-only DataSource (recommendationsDataSource)
     * @return JdbcTemplate для чтения данных рекомендаций
     */
    @Bean(name = "recommendationsJdbcTemplate")
    public JdbcTemplate recommendationsJdbcTemplate(
            @Qualifier("recommendationsDataSource") DataSource dataSource
    ) {
        return new JdbcTemplate(dataSource);
    }

    /**
     * Основной {@link JdbcTemplate}, работающий с defaultDataSource.
     *
     * @param dataSource основной DataSource
     * @return основной JdbcTemplate
     */
    @Bean
    @Primary
    public JdbcTemplate jdbcTemplate(@Qualifier("defaultDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
