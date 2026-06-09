package com.alight.healthbundle.model;

import com.fasterxml.jackson.annotation.*;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class OutOfPocketBreakdown {
    @Field("byCategory")
    @JsonProperty("byCategory")
    @JsonAlias("by_category")
    private Map<String, BigDecimal> byCategory;

    @Field("byPerson")
    @JsonProperty("byPerson")
    @JsonAlias("by_person")
    private Map<String, BigDecimal> byPerson;

    @Field("byProduct")
    @JsonProperty("byProduct")
    @JsonAlias("by_product")
    private Map<String, BigDecimal> byProduct;
}
