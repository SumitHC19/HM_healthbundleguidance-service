package com.alight.healthbundle.model;

import com.fasterxml.jackson.annotation.*;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
public class AccountAdvantage {
    @Field("employeeContributions")
    @JsonProperty("employeeContributions")
    @JsonAlias("employee_contributions")
    private BigDecimal employeeContributions;

    @Field("employerContributions")
    @JsonProperty("employerContributions")
    @JsonAlias("employer_contributions")
    private BigDecimal employerContributions;

    @Field("employerContributionsPercentage")
    @JsonProperty("employerContributionsPercentage")
    @JsonAlias("employer_contributions_percentage")
    private BigDecimal employerContributionsPercentage;

    @Field("taxSavings")
    @JsonProperty("taxSavings")
    @JsonAlias("tax_savings")
    private BigDecimal taxSavings;

    @Field("taxSavingsPercentage")
    @JsonProperty("taxSavingsPercentage")
    @JsonAlias("tax_savings_percentage")
    private BigDecimal taxSavingsPercentage;
}
