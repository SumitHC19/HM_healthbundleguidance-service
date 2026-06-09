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
public class AccountBalances {
    @Field("fsaSpouse")
    @JsonProperty("fsaSpouse")
    @JsonAlias("fsa_spouse")
    private BigDecimal fsaSpouse;

    @Field("fsaSubscriber")
    @JsonProperty("fsaSubscriber")
    @JsonAlias("fsa_subscriber")
    private BigDecimal fsaSubscriber;

    @Field("fsaTotal")
    @JsonProperty("fsaTotal")
    @JsonAlias("fsa_total")
    private BigDecimal fsaTotal;

    @Field("hraSpouse")
    @JsonProperty("hraSpouse")
    @JsonAlias("hra_spouse")
    private BigDecimal hraSpouse;

    @Field("hraSubscriber")
    @JsonProperty("hraSubscriber")
    @JsonAlias("hra_subscriber")
    private BigDecimal hraSubscriber;

    @Field("hraTotal")
    @JsonProperty("hraTotal")
    @JsonAlias("hra_total")
    private BigDecimal hraTotal;

    @Field("hsaSpouse")
    @JsonProperty("hsaSpouse")
    @JsonAlias("hsa_spouse")
    private BigDecimal hsaSpouse;

    @Field("hsaSubscriber")
    @JsonProperty("hsaSubscriber")
    @JsonAlias("hsa_subscriber")
    private BigDecimal hsaSubscriber;

    @Field("hsaTotal")
    @JsonProperty("hsaTotal")
    @JsonAlias("hsa_total")
    private BigDecimal hsaTotal;

    @Field("lpfsaSpouse")
    @JsonProperty("lpfsaSpouse")
    @JsonAlias("lpfsa_spouse")
    private BigDecimal lpfsaSpouse;

    @Field("lpfsaSubscriber")
    @JsonProperty("lpfsaSubscriber")
    @JsonAlias("lpfsa_subscriber")
    private BigDecimal lpfsaSubscriber;

    @Field("lpfsaTotal")
    @JsonProperty("lpfsaTotal")
    @JsonAlias("lpfsa_total")
    private BigDecimal lpfsaTotal;

}
