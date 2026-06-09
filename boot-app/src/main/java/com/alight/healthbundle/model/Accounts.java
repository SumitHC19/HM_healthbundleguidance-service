package com.alight.healthbundle.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
public class Accounts {
    @Field("balanceUsage")
    @JsonProperty("balanceUsage")
    @JsonAlias("balance_usage")
    @Valid
    private AccountBalances balanceUsage;

    @Field("employeeContributions")
    @JsonProperty("employeeContributions")
    @JsonAlias("employee_contributions")
    @Valid
    private AccountBalances employeeContributions;

    @Field("employerContributions")
    @JsonProperty("employerContributions")
    @JsonAlias("employer_contributions")
    @Valid
    private AccountBalances employerContributions;

    @Field("employerMatching")
    @JsonProperty("employerMatching")
    @JsonAlias("employer_matching")
    @Valid
    private AccountBalances employerMatching;

    @Field("rollovers")
    @JsonProperty("rollovers")
    @JsonAlias("rollovers")
    @Valid
    private AccountBalances rollovers;
}
