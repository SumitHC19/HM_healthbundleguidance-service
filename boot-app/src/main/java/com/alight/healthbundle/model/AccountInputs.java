package com.alight.healthbundle.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
public class AccountInputs {

    @JsonProperty("type")
    @NotBlank(message = "Account type is required")
    private String type;

    @JsonProperty("owner")
    private String owner;

    @JsonProperty("starting_balance")
    private BigDecimal startingBalance;

    @JsonProperty("contribution_ytd")
    private BigDecimal contributionYtd;

    @JsonProperty("employer_contribution_ytd")
    private BigDecimal employerContributionYtd;
}
