package ru.skypro.teamwork.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skypro.teamwork.model.DynamicRule;

import java.util.UUID;

public interface DynamicRuleRepository extends JpaRepository<DynamicRule, UUID> {
    long deleteByProductId(UUID productId);
}