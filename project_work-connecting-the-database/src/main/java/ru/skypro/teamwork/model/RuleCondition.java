// `project_work-connecting-the-database/src/main/java/ru/skypro/teamwork/model/RuleCondition.java`
package ru.skypro.teamwork.model;

import lombok.*;
import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "rule_conditions")
public class RuleCondition {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String query;

    private boolean negate;

    @ManyToOne
    @JoinColumn(name = "dynamic_rule_id")
    private DynamicRule dynamicRule;

    @OneToMany(mappedBy = "ruleCondition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RuleConditionArgument> arguments;
}