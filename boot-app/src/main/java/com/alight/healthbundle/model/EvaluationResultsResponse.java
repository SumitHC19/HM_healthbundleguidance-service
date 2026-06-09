package com.alight.healthbundle.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

/**
 * Response model matching OpenAPI 3.0.3 swagger specification.
 * Represents the top-level evaluation result structure.
 * Extended with metadata fields for data platform tracking.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
public class EvaluationResultsResponse {
    @JsonProperty("eventType")
    @JsonAlias("event_type")
    @NotBlank(message = "eventType is required")
    private String eventType;

    @JsonProperty("evaluationId")
    @JsonAlias("evaluation_id")
    @NotBlank(message = "evaluationId is required")
    private String evaluationId;

    @JsonProperty("evaluationType")
    @JsonAlias("evaluation_type")
    @NotBlank(message = "evaluationType is required")
    private String evaluationType;

    @JsonProperty("evaluationStatus")
    @JsonAlias("evaluation_status")
    @NotBlank(message = "evaluationStatus is required")
    private String evaluationStatus;

    @JsonProperty("evaluationOutput")
    @JsonAlias("evaluation_output")
    @NotNull(message = "evaluationOutput is required")
    @Valid
    private EvaluationOutput evaluationOutput;

    // Metadata fields for data platform tracking
    @JsonProperty("source")
    private String source;

    @JsonProperty("savedAt")
    private LocalDateTime savedAt;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonProperty("documentStatus")
    private String documentStatus;
}
