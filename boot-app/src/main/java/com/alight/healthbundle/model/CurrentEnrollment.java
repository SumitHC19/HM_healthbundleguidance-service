package com.alight.healthbundle.model;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
public class CurrentEnrollment {

    @JsonProperty("plan_id")
    private String planId;

    @JsonProperty("mapped_plan_id")
    @NotBlank(message = "mapped_plan_id is required")
    private String mappedPlanId;

    @JsonProperty("premium")
    private BigDecimal premium;

    @JsonProperty("product_type")
    private String productType;

    @JsonProperty("num_deductions_planyear")
    @NotNull(message = "num_deductions_planyear is required")
    private Integer numDeductionsPlanyear;

    @JsonProperty("num_deductions_remaining")
    private Integer numDeductionsRemaining;

    @JsonProperty("coverage_start_date")
    private String coverageStartDate;

    @JsonProperty("coverage_end_date")
    private String coverageEndDate;

    @JsonProperty("coverage_id")
    private String coverageId;

    @JsonProperty("covered_people")
    @Valid
    private List<CoveredPerson> coveredPeople;

    @JsonProperty("employer_contribution_planyear")
    private BigDecimal employerContributionPlanyear;

    @JsonProperty("employer_contribution_remaining")
    private BigDecimal employerContributionRemaining;

    @JsonProperty("contribution")
    private BigDecimal contribution;

    @JsonProperty("balance")
    private BigDecimal balance;

}
