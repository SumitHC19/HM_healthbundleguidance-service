package com.alight.healthbundle.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Represents an eligible offer for coverage choice or health account.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EligibleOffer {

    @JsonProperty("offer_type")
    @NotBlank(message = "Offer type is required")
    private String offerType;

    @JsonProperty("plan_id")
    @NotBlank(message = "Plan ID is required")
    private String planId;

    @JsonProperty("offer_id")
    @NotBlank(message = "Offer ID is required")
    private String offerId;

    @JsonProperty("decline_plan_id")
    private String declinePlanId;

    @JsonProperty("offer_from")
    private String offerFrom;

    @JsonProperty("num_deductions_planyear")
    @NotNull(message = "Number of deductions per plan year is required")
    private Integer numDeductionsPlanyear;

    @JsonProperty("num_deductions_remaining")
    @NotNull(message = "Number of remaining deductions is required")
    private Integer numDeductionsRemaining;

    @JsonProperty("coverage_start_date")
    @NotBlank(message = "Coverage start date is required")
    private String coverageStartDate;

    @JsonProperty("coverage_end_date")
    @NotBlank(message = "Coverage end date is required")
    private String coverageEndDate;

    @JsonProperty("is_section125")
    private Boolean isSection125;

    @JsonProperty("is_mandatory")
    private Boolean isMandatory;

    @JsonProperty("contribution_limit_planyear_single")
    private BigDecimal contributionLimitPlanyearSingle;

    @JsonProperty("contribution_limit_planyear_family")
    private BigDecimal contributionLimitPlanyearFamily;

    @JsonProperty("employer_match_rate")
    private BigDecimal employerMatchRate;

    @JsonProperty("min_contribution_planyear")
    private Integer minContributionPlanyear;

    @JsonProperty("incompatible_with")
    private List<Map<String, Object>> incompatibleWith;

    @JsonProperty("must_include_all")
    private List<Map<String, Object>> mustIncludeAll;

    @JsonProperty("requires_all_of")
    private List<Map<String, Object>> requiresAllOf;

    @JsonProperty("requires_one_of")
    private List<Map<String, Object>> requiresOneOf;

    @JsonProperty("coverage_choices")
    private List<Map<String, Object>> coverageChoices;

    @JsonProperty("employer_contributions")
    private List<Map<String, Object>> employerContributions;

    @JsonProperty("covered_people")
    private List<Map<String, Object>> coveredPeople;

    @JsonProperty("level_choices")
    private List<Map<String, Object>> levelChoices;

    private BigDecimal level;

    @JsonProperty("level_spouse")
    private BigDecimal levelSpouse;

    @JsonProperty("level_child")
    private BigDecimal levelChild;

    @JsonProperty("guaranteed_issue_level")
    private Integer guaranteedIssueLevel;

    @JsonProperty("min_level_percent_of_subscriber")
    private BigDecimal minLevelPercentOfSubscriber;

    @JsonProperty("max_level_percent_of_subscriber")
    private BigDecimal maxLevelPercentOfSubscriber;

}
