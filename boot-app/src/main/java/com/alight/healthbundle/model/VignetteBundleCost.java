package com.alight.healthbundle.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class VignetteBundleCost {
    @Field("employeeContributions")
    @JsonProperty("employeeContributions")
    @JsonAlias("employee_contributions")
    @Valid
    private EmployeeContributions employeeContributions;

    @Field("outOfPocket")
    @JsonProperty("outOfPocket")
    @JsonAlias("outofpocket")
    private BigDecimal outOfPocket;

    @Field("premiums")
    @JsonProperty("premiums")
    @JsonAlias("premiums")
    private BigDecimal premiums;

    @Field("total")
    @JsonProperty("total")
    @JsonAlias("total")
    private BigDecimal total;

    @Field("totalPerPaycheck")
    @JsonProperty("totalPerPaycheck")
    @JsonAlias("total_per_paycheck")
    private BigDecimal totalPerPaycheck;

    @Field("type")
    @JsonProperty("type")
    @JsonAlias("type")
    private String type;
}
