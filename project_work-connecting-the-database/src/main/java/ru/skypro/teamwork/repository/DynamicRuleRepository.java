package ru.skypro.teamwork.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skypro.teamwork.model.DynamicRule;

import java.util.UUID;

/**
 * Репозиторий JPA для сущности {@link DynamicRule}.
 * Предоставляет стандартные CRUD операции и удаление по идентификатору продукта.
 */
public interface DynamicRuleRepository extends JpaRepository<DynamicRule, UUID> {
    /**
     * Удаляет динамическое правило по идентификатору продукта.
     *
     * @param productId UUID продукта
     * @return количество удалённых записей
     */
    long deleteByProductId(UUID productId);
}