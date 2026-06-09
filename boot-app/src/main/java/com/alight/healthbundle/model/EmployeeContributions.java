package com.alight.healthbundle.model;

import com.fasterxml.jackson.annotation.*;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class EmployeeContributions {
    @Field("fsa")
    @JsonProperty("fsa")
    @JsonAlias("fsa")
    private BigDecimal fsa;

    @Field("hsa")
    @JsonProperty("hsa")
    @JsonAlias("hsa")
    private BigDecimal hsa;

    @Field("lpfsa")
    @JsonProperty("lpfsa")
    @JsonAlias("lpfsa")
    private BigDecimal lpfsa;
}
