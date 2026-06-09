package com.alight.healthbundle.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
public class EvaluationOutput {
    @JsonProperty("bundles")
    @JsonAlias("bundles")
    // @NotNull(message = "bundles array is required")
    @Valid
    private List<Bundle> bundles;

    @JsonProperty("errorCode")
    @JsonAlias("error_code")
    private String errorCode;

    @JsonProperty("errorMessage")
    @JsonAlias("error_message")
    private String errorMessage;

    @JsonProperty("errorDetails")
    @JsonAlias("error_details")
    private String errorDetails;

}
