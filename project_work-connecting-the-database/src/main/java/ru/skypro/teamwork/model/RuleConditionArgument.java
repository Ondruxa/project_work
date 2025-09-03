package ru.skypro.teamwork.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "rule_condition_arguments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class RuleConditionArgument {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID id;

    @Column(name = "argument")
    @ToString.Include
    private String argument;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_condition_id")
    private RuleCondition ruleCondition;
}