package com.alight.healthbundle.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class EstimatedCosts {
    @Field("adverse")
    @JsonProperty("adverse")
    @JsonAlias("adverse")
    @Valid
    private ScenarioCosts adverse;

    @Field("expected")
    @JsonProperty("expected")
    @JsonAlias("expected")
    @Valid
    private ScenarioCosts expected;

}
