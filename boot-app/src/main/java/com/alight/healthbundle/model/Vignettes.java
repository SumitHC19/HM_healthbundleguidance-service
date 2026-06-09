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
public class Vignettes {
    @Field("bundlePlanCosts")
    @JsonProperty("bundlePlanCosts")
    @JsonAlias("bundle_plan_costs")
    @Valid
    private BundlePlanCosts bundlePlanCosts;

    @Field("closestComparison")
    @JsonProperty("closestComparison")
    @JsonAlias("closest_comparison")
    private ClosestComparison closestComparison; // keep flexible per sample

    @Field("growthIllustration")
    @JsonProperty("growthIllustration")
    @JsonAlias("growth_illustration")
    @Valid
    private GrowthIllustration growthIllustration;

    @Field("healthAccountAdvantages")
    @JsonProperty("healthAccountAdvantages")
    @JsonAlias("health_account_advantages")
    @Valid
    private HealthAccountAdvantages healthAccountAdvantages;
}
