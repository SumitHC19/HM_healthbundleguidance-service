package com.alight.asg.model.header.v1_0;

import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Vendored RequestHeader from Alight ASG model library.
 * Represents the alightRequestHeader structure.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestHeader {

    @JsonIgnore
    private static ObjectMapper OBJECT_MAPPER = new ObjectMapper().setTimeZone(TimeZone.getDefault())
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    @JsonIgnore
    private static ObjectReader OBJECT_READER = OBJECT_MAPPER.readerFor(RequestHeader.class);
    @JsonIgnore
    private static ObjectWriter OBJECT_WRITER = OBJECT_MAPPER.writerFor(RequestHeader.class);

    @JsonProperty("clientId")
    private String clientId;

    @JsonProperty("systemInstanceId")
    private String systemInstanceId;

    @JsonProperty("subjectId")
    private String subjectId;

    @JsonProperty("subjectType")
    private String subjectType;

    @JsonProperty("locale")
    private String locale;

    @JsonProperty("roleId")
    private String roleId;

    @JsonProperty("channelRequestData")
    private String channelRequestData;

    @JsonProperty("consumerReferenceId")
    private String consumerReferenceId;

    // Getters and setters
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getSystemInstanceId() {
        return systemInstanceId;
    }

    public void setSystemInstanceId(String systemInstanceId) {
        this.systemInstanceId = systemInstanceId;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(String subjectType) {
        this.subjectType = subjectType;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getChannelRequestData() {
        return channelRequestData;
    }

    public void setChannelRequestData(String channelRequestData) {
        this.channelRequestData = channelRequestData;
    }

    public String getConsumerReferenceId() {
        return consumerReferenceId;
    }

    public void setConsumerReferenceId(String consumerReferenceId) {
        this.consumerReferenceId = consumerReferenceId;
    }

    /**
     * Parse JSON string into RequestHeader object.
     */
    public static RequestHeader parse(String json) throws Exception {
        if (json == null || json.isBlank()) {
            throw new IllegalArgumentException("Request header JSON cannot be null or blank");
        }
        ObjectMapper mapper = new ObjectMapper();
        return OBJECT_READER.readValue(TokenDecoderUtil.decodeIfNeeded(json), RequestHeader.class);
        // return mapper.readValue(json, RequestHeader.class);
    }

    /**
     * Convert RequestHeader to JSON string.
     */
    public String toJson() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }
}
