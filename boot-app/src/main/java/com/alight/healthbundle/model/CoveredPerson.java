package com.alight.healthbundle.model;

import com.fasterxml.jackson.annotation.*;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
public class CoveredPerson {
    @Field("id")
    @JsonProperty("id")
    @JsonAlias("id")
    private String id;

    @Field("level")
    @JsonProperty("level")
    @JsonAlias("level")
    private BigDecimal level;
}
