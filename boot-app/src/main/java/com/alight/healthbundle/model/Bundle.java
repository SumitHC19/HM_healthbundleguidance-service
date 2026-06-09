package com.alight.healthbundle.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
public class Bundle {
    @Field("bundleRank")
    @JsonProperty("bundleRank")
    @JsonAlias("bundle_rank")
    private Integer bundleRank;

    @Field("estimatedCosts")
    @JsonProperty("estimatedCosts")
    @JsonAlias("estimated_costs")
    @Valid
    private EstimatedCosts estimatedCosts;

    @Field("featuredAs")
    @JsonProperty("featuredAs")
    @JsonAlias("featured_as")
    private String featuredAs;

    @Field("featuredAsVariants")
    @JsonProperty("featuredAsVariants")
    @JsonAlias("featured_as_variants")
    private List<String> featuredAsVariants;

    @Field("isCustomizable")
    @JsonProperty("isCustomizable")
    @JsonAlias("is_customizable")
    private Boolean isCustomizable;

    @Field("planUses")
    @JsonProperty("planUses")
    @JsonAlias("plan_uses")
    @Valid
    private List<PlanUse> planUses;

    @Field("productUses")
    @JsonProperty("productUses")
    @JsonAlias("product_uses")
    @Valid
    private List<ProductUse> productUses;

    @Field("vignettes")
    @JsonProperty("vignettes")
    @JsonAlias("vignettes")
    @Valid
    private Vignettes vignettes;
}
