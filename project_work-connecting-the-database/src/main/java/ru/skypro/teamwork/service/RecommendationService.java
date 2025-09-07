package ru.skypro.teamwork.service;

import org.springframework.transaction.annotation.Transactional;
import ru.skypro.teamwork.dto.RecommendationDto;
import ru.skypro.teamwork.dto.RecommendationListDto;
import ru.skypro.teamwork.model.DynamicRule;
import ru.skypro.teamwork.model.RuleCondition;
import ru.skypro.teamwork.model.RuleConditionArgument;
import ru.skypro.teamwork.repository.DynamicRuleRepository;
import ru.skypro.teamwork.repository.RecommendationsRepository;
import ru.skypro.teamwork.service.ruleset.RecommendationRuleSetService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final List<RecommendationRuleSetService> ruleSets;
    private final DynamicRuleRepository dynamicRuleRepository;
    private final RecommendationsRepository recommendationsRepository;
    private final UserLookupService userLookupService;

    public RecommendationService(
            List<RecommendationRuleSetService> ruleSets,
            DynamicRuleRepository dynamicRuleRepository,
            RecommendationsRepository recommendationsRepository,
            UserLookupService userLookupService
    ) {
        this.ruleSets = ruleSets;
        this.dynamicRuleRepository = dynamicRuleRepository;
        this.recommendationsRepository = recommendationsRepository;
        this.userLookupService = userLookupService;
    }

    @Transactional
    public RecommendationListDto getRecommendations(UUID userId) {
        List<RecommendationDto> recommendations = ruleSets.stream()
                .map(ruleSet -> ruleSet.applyRule(userId))
                .flatMap(Optional::stream)
                .collect(Collectors.toList());

        List<DynamicRule> dynamicRules = dynamicRuleRepository.findAll();
        for (DynamicRule rule : dynamicRules) {
            boolean rulePassed = true;
            if (rule.getRule() != null) {
                for (RuleCondition condition : rule.getRule()) {
                    rulePassed &= checkCondition(userId, condition);
                }
            }
            if (rulePassed) {
                recommendations.add(new RecommendationDto(
                        UUID.fromString(rule.getProductId()),
                        rule.getProductName(),
                        rule.getProductText()
                ));
            }
        }

        var userInfo = userLookupService.findById(userId)
                .orElse(new UserLookupService.UserInfo(userId, "", ""));

        return new RecommendationListDto(
                userId,
                userInfo.firstName(),
                userInfo.lastName(),
                recommendations
        );
    }

    private boolean checkCondition(UUID userId, RuleCondition condition) {
        List<String> args = new ArrayList<>();
        if (condition.getArguments() != null) {
            for (RuleConditionArgument arg : condition.getArguments()) {
                args.add(arg.getArgument());
            }
        }
        switch (condition.getQuery()) {
            case "USER_OF":
                return checkUserOf(userId, args, condition.isNegate());
            case "ACTIVE_USER_OF":
                return checkActiveUserOf(userId, args, condition.isNegate());
            case "TRANSACTION_SUM_COMPARE":
                return checkTransactionSumCompare(userId, args, condition.isNegate());
            case "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW":
                return checkDepositWithdrawCompare(userId, args, condition.isNegate());
            default:
                return false;
        }
    }

    private boolean checkUserOf(UUID userId, List<String> args, boolean negate) {
        String productType = args.get(0);
        boolean result = recommendationsRepository.userHasProductType(userId, productType);
        return negate ? !result : result;
    }

    private boolean checkActiveUserOf(UUID userId, List<String> args, boolean negate) {
        String productType = args.get(0);
        boolean result = recommendationsRepository.userHasActiveProductType(userId, productType, 5);
        return negate ? !result : result;
    }

    private boolean checkTransactionSumCompare(UUID userId, List<String> args, boolean negate) {
        String productType = args.get(0);
        String transactionType = args.get(1);
        String operator = args.get(2);
        int value = Integer.parseInt(args.get(3));
        int sum = recommendationsRepository.getTransactionSum(userId, productType, transactionType);
        boolean result = compare(sum, operator, value);
        return negate ? !result : result;
    }

    private boolean checkDepositWithdrawCompare(UUID userId, List<String> args, boolean negate) {
        String productType = args.get(0);
        String operator = args.get(1);
        int depositSum = recommendationsRepository.getTransactionSum(userId, productType, "DEPOSIT");
        int withdrawSum = recommendationsRepository.getTransactionSum(userId, productType, "WITHDRAW");
        boolean result = compare(depositSum, operator, withdrawSum);
        return negate ? !result : result;
    }

    private boolean compare(int left, String operator, int right) {
        switch (operator) {
            case ">": return left > right;
            case "<": return left < right;
            case "=": return left == right;
            case ">=": return left >= right;
            case "<=": return left <= right;
            default: return false;
        }
    }
}