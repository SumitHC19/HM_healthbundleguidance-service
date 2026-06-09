package com.alight.healthbundle.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Subscriber (primary person) information for evaluation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscriber {

    @NotBlank(message = "Subscriber ID is required")
    private String id;

    @NotBlank(message = "Birthdate is required")
    private String birthdate;

    @JsonProperty("annual_pay")
    @NotNull(message = "Annual pay is required")
    private BigDecimal annualPay;

    @JsonProperty("other_income")
    private BigDecimal otherIncome;

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

    private String country;

    @JsonProperty("tax_state")
    // @NotBlank(message = "Tax state is required")
    private String taxState;

    @JsonProperty("pay_periods_planyear")
    @NotNull(message = "Pay periods per plan year is required")
    private Integer payPeriodsPlanyear;

    @JsonProperty("pay_periods_remaining")
    @NotNull(message = "Remaining pay periods is required")
    private Integer payPeriodsRemaining;

    private Map<String, Object> healthcare;
}
