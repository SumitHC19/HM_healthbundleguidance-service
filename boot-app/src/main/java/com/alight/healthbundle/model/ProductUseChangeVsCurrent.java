package com.alight.healthbundle.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
public class ProductUseChangeVsCurrent {
    @Field("premiumChange")
    @JsonProperty("premiumChange")
    @JsonAlias("premium_change")
    private BigDecimal premiumChange;

    @Field("coverageChanges")
    @JsonProperty("coverageChanges")
    @JsonAlias("coverage_changes")
    @Valid
    private List<CoverageChange> coverageChanges;

    @Field("employeeContributionChange")
    @JsonProperty("employeeContributionChange")
    @JsonAlias("employee_contribution_change")
    private BigDecimal employeeContributionChange;
}
