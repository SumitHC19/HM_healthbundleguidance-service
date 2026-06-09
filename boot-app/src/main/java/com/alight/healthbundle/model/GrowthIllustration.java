package com.alight.healthbundle.model;

import com.fasterxml.jackson.annotation.*;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
public class GrowthIllustration {
    @Field("monthlyContribution")
    @JsonProperty("monthlyContribution")
    @JsonAlias("monthly_contribution")
    private BigDecimal monthlyContribution;

    @Field("basis")
    @JsonProperty("basis")
    @JsonAlias("basis")
    private List<BigDecimal> basis;

    @Field("totalBalance")
    @JsonProperty("totalBalance")
    @JsonAlias("total_balance")
    private List<BigDecimal> totalBalance;

    @Field("illustrativeMonthlyReturn")
    @JsonProperty("illustrativeMonthlyReturn")
    @JsonAlias("illustrative_monthly_return")
    private BigDecimal illustrativeMonthlyReturn;

    @Field("durationYears")
    @JsonProperty("durationYears")
    @JsonAlias("duration_years")
    private Integer durationYears;

    @Field("irsLimit")
    @JsonProperty("irsLimit")
    @JsonAlias("irs_limit")
    private BigDecimal irsLimit;

    @Field("initialBalance")
    @JsonProperty("initialBalance")
    @JsonAlias("initial_balance")
    private BigDecimal initialBalance;
}
