package ru.skypro.teamwork.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "dynamic_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class DynamicRule {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID id;

    @Column(name = "product_id", nullable = false)
    @ToString.Include
    private UUID productId;

    @Column(name = "product_name", nullable = false)
    @ToString.Include
    private String productName;

    @Column(name = "product_text", length = 2000)
    @ToString.Include
    private String productText;

    @OneToMany(mappedBy = "dynamicRule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RuleCondition> rule;
}