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
public class ProductUse {
    @Field("coverageFrom")
    @JsonProperty("coverageFrom")
    @JsonAlias("coverage_from")
    private String coverageFrom;

    @Field("coveredPeople")
    @JsonProperty("coveredPeople")
    @JsonAlias("covered_people")
    @Valid
    private List<CoveredPerson> coveredPeople;

    @Field("employeeContribution")
    @JsonProperty("employeeContribution")
    @JsonAlias("employee_contribution")
    private BigDecimal employeeContribution;

    @Field("employerContribution")
    @JsonProperty("employerContribution")
    @JsonAlias("employer_contribution")
    private BigDecimal employerContribution;

    @Field("excludedPeopleIds")
    @JsonProperty("excludedPeopleIds")
    @JsonAlias("excluded_people_ids")
    private List<String> excludedPeopleIds;

    @Field("ineligiblePeopleIds")
    @JsonProperty("ineligiblePeopleIds")
    @JsonAlias("ineligible_people_ids")
    private List<String> ineligiblePeopleIds;

    @Field("numDeductionsPlanyear")
    @JsonProperty("numDeductionsPlanyear")
    @JsonAlias("num_deductions_planyear")
    private Integer numDeductionsPlanyear;

    @Field("numDeductionsRemaining")
    @JsonProperty("numDeductionsRemaining")
    @JsonAlias("num_deductions_remaining")
    private Integer numDeductionsRemaining;

    @Field("premium")
    @JsonProperty("premium")
    @JsonAlias("premium")
    private BigDecimal premium;

    @Field("productId")
    @JsonProperty("productId")
    @JsonAlias("product_id")
    private String productId;

    @Field("totalPremiums")
    @JsonProperty("totalPremiums")
    @JsonAlias("total_premiums")
    private BigDecimal totalPremiums;

    @Field("type")
    @JsonProperty("type")
    @JsonAlias("type")
    private String type;

    @Field("vsCurrent")
    @JsonProperty("vsCurrent")
    @JsonAlias("vs_current")
    @Valid
    private ProductUseChangeVsCurrent vsCurrent;

}
