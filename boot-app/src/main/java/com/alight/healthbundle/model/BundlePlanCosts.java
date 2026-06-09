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
public class BundlePlanCosts {
    @Field("baseBundle")
    @JsonProperty("baseBundle")
    @JsonAlias("base_bundle")
    @Valid
    private VignetteBundleCost baseBundle;

    public VignetteBundleCost getBaseBundle() {
        return baseBundle;
    }

    public void setBaseBundle(VignetteBundleCost baseBundle) {
        this.baseBundle = baseBundle;
    }

}
