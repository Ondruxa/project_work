package ru.skypro.teamwork.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RuleStatsResponseDto {
    private List<RuleStatsItemDto> stats;
}
