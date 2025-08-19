package ru.skypro.teamwork.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Data
@Entity
@Table(name = "rule_condition_arguments")
public class RuleConditionArgument {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "argument")
    private String argument;

    @ManyToOne
    @JoinColumn(name = "rule_condition_id")
    private RuleCondition ruleCondition;
}