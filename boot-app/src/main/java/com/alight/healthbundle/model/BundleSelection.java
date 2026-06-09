package com.alight.healthbundle.model;

import org.springframework.data.mongodb.core.mapping.Document;

import com.alight.healthbundle.model.enums.FeaturedAs;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@Document(collection = "bundleSelection")
public class BundleSelection {

    private String id;
    // @NotBlank(message = "evaluationId must be present")
    private String evaluationId;

    @NotBlank(message = "businessProcessReferenceId must be present")
    private String businessProcessReferenceId;

    // Extracted from alightRequestHeader, not required in request body
    @NotBlank(message = "clientId must be present")
    private String clientId;

    // Extracted from token or request parameter/body, not required in request body

    @NotBlank(message = "platformInternalId must be present")
    private String platformInternalId;

    @NotBlank(message = "featuredAs must be present")
    private String featuredAs;

    private String planYearBeginDate;

    @JsonProperty("lastModifiedTimeStamp")
    private String lastModifiedTimeStamp;
}
