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
public class HealthAccountAdvantages {
    @Field("accountTypes")
    @JsonProperty("accountTypes")
    @JsonAlias("account_types")
    private String accountTypes;

    @Field("fsa")
    @JsonProperty("fsa")
    @JsonAlias("fsa")
    @Valid
    private AccountAdvantage fsa;

    @Field("hsa")
    @JsonProperty("hsa")
    @JsonAlias("hsa")
    @Valid
    private AccountAdvantage hsa;

    @Field("lpfsa")
    @JsonProperty("lpfsa")
    @JsonAlias("lpfsa")
    @Valid
    private AccountAdvantage lpfsa;

    @Field("total")
    @JsonProperty("total")
    @JsonAlias("total")
    @Valid
    private AccountAdvantage total;
}
