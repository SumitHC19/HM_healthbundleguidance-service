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
public class ClosestComparison {

    @Field("baseBundle")
    @JsonProperty("baseBundle")
    @JsonAlias("base_bundle")
    @Valid
    private VignetteBundleCost baseBundle;

    @Field("alternateBundle")
    @JsonProperty("alternateBundle")
    @JsonAlias("alternate_bundle")
    @Valid
    private VignetteBundleCost alternateBundle;
}
