package com.alight.healthbundle.model;

import com.fasterxml.jackson.annotation.*;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class CoverageChange {
    @Field("changeType")
    @JsonProperty("changeType")
    @JsonAlias("change_type")
    private String changeType;

    @Field("personId")
    @JsonProperty("personId")
    @JsonAlias("person_id")
    private String personId;
}
