package com.alight.healthbundle.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * SAVVI evaluation request containing plan year details, subscriber info,
 * coverable people, products, and eligible offers.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SAVVIRequest {

    @JsonProperty("sponsor_id")
    private String sponsorId;

    @JsonProperty("plan_year_start_date")
    @NotBlank(message = "Plan year start date is required")
    private String planYearStartDate;

    @JsonProperty("plan_year_end_date")
    @NotBlank(message = "Plan year end date is required")
    private String planYearEndDate;

    @JsonProperty("enrollment_mode")
    @NotBlank(message = "Enrollment mode is required")
    private String enrollmentMode;

    @JsonProperty("effective_date")
    @NotBlank(message = "Effective date is required")
    private String effectiveDate;

    @NotNull(message = "Subscriber information is required")
    @Valid
    private Subscriber subscriber;

    @JsonProperty("coverable_people")
    // @NotEmpty(message = "At least one coverable person is required")
    @Valid
    private List<Person> coverablePeople;

    @JsonProperty("accounts")
    @Valid
    private List<AccountInputs> accounts;

    @NotEmpty(message = "At least one product is required")
    @Valid
    private List<Product> products;

    @JsonProperty("eligible_offers")
    @Valid
    private List<EligibleOffer> eligibleOffers;

    @JsonProperty("current_enrollments")
    @Valid
    private List<CurrentEnrollment> currentEnrollments;

    private Map<String, Object> customizations;
}
