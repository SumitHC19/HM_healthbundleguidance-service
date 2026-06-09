package com.alight.healthbundle.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
public class ScenarioCosts {
    @Field("accounts")
    @JsonProperty("accounts")
    @JsonAlias("accounts")
    @Valid
    private Accounts accounts;

    @Field("outOfPocketBreakdown")
    @JsonProperty("outOfPocketBreakdown")
    @JsonAlias("outofpocket_breakdown")
    @Valid
    private OutOfPocketBreakdown outOfPocketBreakdown;

    @Field("totalCost")
    @JsonProperty("totalCost")
    @JsonAlias("total_cost")
    private BigDecimal totalCost;

    @Field("totalEmployerContributionsExSihra")
    @JsonProperty("totalEmployerContributionsExSihra")
    @JsonAlias("total_employer_contributions_ex_sihra")
    private BigDecimal totalEmployerContributionsExSihra;

    @Field("totalIndemnityPayouts")
    @JsonProperty("totalIndemnityPayouts")
    @JsonAlias("total_indemnity_payouts")
    private BigDecimal totalIndemnityPayouts;

    @Field("totalIndemnityPremiums")
    @JsonProperty("totalIndemnityPremiums")
    @JsonAlias("total_indemnity_premiums")
    private BigDecimal totalIndemnityPremiums;

    @Field("totalIndemnityWellnessBenefits")
    @JsonProperty("totalIndemnityWellnessBenefits")
    @JsonAlias("total_indemnity_wellness_benefits")
    private BigDecimal totalIndemnityWellnessBenefits;

    @Field("totalOutOfPocket")
    @JsonProperty("totalOutOfPocket")
    @JsonAlias("total_outofpocket")
    private BigDecimal totalOutOfPocket;

    @Field("totalOutOfPocketCredit")
    @JsonProperty("totalOutOfPocketCredit")
    @JsonAlias("total_outofpocket_credit")
    private BigDecimal totalOutOfPocketCredit;

    @Field("totalPremiumCredit")
    @JsonProperty("totalPremiumCredit")
    @JsonAlias("total_premium_credit")
    private BigDecimal totalPremiumCredit;

    @Field("totalPremiums")
    @JsonProperty("totalPremiums")
    @JsonAlias("total_premiums")
    private BigDecimal totalPremiums;

    @Field("totalTaxSavings")
    @JsonProperty("totalTaxSavings")
    @JsonAlias("total_tax_savings")
    private BigDecimal totalTaxSavings;
}
