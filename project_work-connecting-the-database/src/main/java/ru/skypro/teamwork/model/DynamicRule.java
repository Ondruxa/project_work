package ru.skypro.teamwork.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "dynamic_rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DynamicRule {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "product_text", length = 2000)
    private String productText;

    @Column(name = "rule", columnDefinition = "TEXT", nullable = false)
    private String rule;
}