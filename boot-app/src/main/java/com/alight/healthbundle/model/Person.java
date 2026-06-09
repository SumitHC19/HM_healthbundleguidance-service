package com.alight.healthbundle.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a coverable person (spouse, child, etc.) in the evaluation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    @NotBlank(message = "Person ID is required")
    private String id;

    @NotBlank(message = "Birthdate is required")
    private String birthdate;

    @NotBlank(message = "Relationship is required")
    private String relationship;

    private Map<String, Object> healthcare;

    @JsonProperty("annual_pay")
    private BigDecimal annualPay;

    @JsonProperty("monthly_socialsecurity")
    private BigDecimal monthlySocialSecurity;

    @JsonProperty("is_socialsecurity_started")
    private Boolean isSocialSecurityStarted;

    @JsonProperty("socialsecurity_start_age")
    private Integer socialSecurityStartAge;

    @JsonProperty("medicare_status")
    private String medicareStatus;

    @JsonProperty("is_covered")
    private Boolean isCovered;

    @JsonProperty("coverage_exclusions")
    private List<String> coverageExclusions;
}
