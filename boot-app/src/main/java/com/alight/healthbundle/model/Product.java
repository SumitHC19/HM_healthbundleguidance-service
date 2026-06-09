package com.alight.healthbundle.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Represents a product (medical plan, HSA, FSA, etc.) available for selection.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @NotBlank(message = "Product type is required")
    private String type;

    @JsonProperty("product_id")
    @NotBlank(message = "Product ID is required")
    private String productId;

    @JsonProperty("plan_ids")
    @NotEmpty(message = "At least one plan ID is required")
    private List<String> planIds;

    private String kind;

    @JsonProperty("is_hdhp")
    private Boolean isHdhp;

    @JsonProperty("is_investable")
    private Boolean isInvestable;

    @JsonProperty("in_network")
    private Map<String, Object> inNetwork;

    @JsonProperty("out_of_network")
    private Map<String, Object> outOfNetwork;

    @JsonProperty("wellness_benefit")
    private Map<String, Object> wellnessBenefit;

    private Map<String, Object> services;

    private Map<String, Object> covers;

    @JsonProperty("external_level_ids")
    private Map<String, Object> externalLevelIds;

    @JsonProperty("outofpocket_limits")
    private Map<String, Object> outofpocketLimits;

    @JsonProperty("credit_mode")
    private String creditMode;

    @JsonProperty("annual_limit")
    private Integer annualLimit;

    @JsonProperty("lifetime_limit")
    private Integer lifetimeLimit;

    @JsonProperty("payout_starting_on")
    private Integer payoutStartingOn;

    @JsonProperty("eligible_services")
    private List<String> eligibleServices;

    @JsonProperty("expense_order")
    private List<String> expenseOrder;

    @JsonProperty("plan_order")
    private List<String> planOrder;

    @JsonProperty("keep_if_unused")
    private Boolean keepIfUnused;

    @JsonProperty("allow_rx")
    private Boolean allowRx;

    @JsonProperty("allows_in_service_rollovers")
    private Boolean allowsInServiceRollovers;

    @JsonProperty("allows_retirement_rollover")
    private Boolean allowsRetirementRollover;
}
