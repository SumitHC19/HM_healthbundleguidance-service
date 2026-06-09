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
@Getter
@Setter
public class PlanUse {
    @Field("coverageFrom")
    @JsonProperty("coverageFrom")
    @JsonAlias("coverage_from")
    private String coverageFrom;

    @Field("coverageId")
    @JsonProperty("coverageId")
    @JsonAlias("coverage_id")
    private String coverageId;

    @Field("coveredPeople")
    @JsonProperty("coveredPeople")
    @JsonAlias("covered_people")
    @Valid
    private List<CoveredPerson> coveredPeople;

    @Field("employeeContribution")
    @JsonProperty("employeeContribution")
    @JsonAlias("employee_contribution")
    private BigDecimal employeeContribution;

    @Field("offerId")
    @JsonProperty("offerId")
    @JsonAlias("offer_id")
    private String offerId;

    @Field("planId")
    @JsonProperty("planId")
    @JsonAlias("plan_id")
    private String planId;

    @Field("productId")
    @JsonProperty("productId")
    @JsonAlias("product_id")
    private String productId;

    @Field("type")
    @JsonProperty("type")
    @JsonAlias("type")
    private String type;
}
