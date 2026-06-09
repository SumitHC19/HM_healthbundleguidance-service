package com.alight.healthbundle.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Request/Response model for bundle evaluation input data.
 * Contains subscriber, coverable people, products, and eligible offers for
 * SAVVI evaluation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EvaluationInputRequest {

    // Extracted from alightRequestHeader, not required in request body
    private String clientId;

    // Extracted from token or alightRequestHeader, not required in request body
    private String platformInternalId;

    @NotBlank(message = "evaluationId is required")
    private String evaluationId;

    @NotBlank(message = "businessProcessReferenceId is required")
    private String businessProcessReferenceId;

    @NotNull(message = "savviRequest data is required")
    @Valid
    @JsonProperty("savviRequest")
    private SAVVIRequest savviRequest;

    private String timestamp;

    private LocalDateTime savedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonProperty("documentStatus")
    private String documentStatus;
}
