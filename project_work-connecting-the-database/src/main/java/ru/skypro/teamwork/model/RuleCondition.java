package ru.skypro.teamwork.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

/**
 * Сущность отдельного условия динамического правила.
 * <p>
 * Содержит текст запроса (query), флаг negate,
 * ссылку на родительское правило {@link DynamicRule} и список аргументов {@link RuleConditionArgument}.
 */
@Entity
@Table(name = "rule_conditions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class RuleCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID id;

    @ToString.Include
    private String query;

    private boolean negate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dynamic_rule_id")
    private DynamicRule dynamicRule;

    @OneToMany(mappedBy = "ruleCondition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RuleConditionArgument> arguments;
}