package com.alight.asg.model.token.v1_0;

import com.alight.asg.model.header.v1_0.TokenDecoderUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDate;
import java.util.List;
import java.util.TimeZone;

/**
 * Vendored PersonSessionToken from Alight ASG model library.
 * Represents the alightPersonSessionToken structure.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonSessionToken {

    @JsonIgnore
    private static ObjectMapper OBJECT_MAPPER = new ObjectMapper().setTimeZone(TimeZone.getDefault())
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    @JsonIgnore
    private static ObjectReader OBJECT_READER = OBJECT_MAPPER.readerFor(PersonSessionToken.class);
    @JsonIgnore
    private static ObjectWriter OBJECT_WRITER = OBJECT_MAPPER.writerFor(PersonSessionToken.class);

    @JsonProperty("accessToken")
    private String accessToken;

    @JsonProperty("sessionId")
    private String sessionId;

    @JsonProperty("expires")
    private Long expires;

    @JsonProperty("brokerUserId")
    private String brokerUserId;

    @JsonProperty("testCfg")
    private String testCfg;

    @JsonProperty("locale")
    private String locale;

    @JsonProperty("systemDate")
    private LocalDate systemDate;

    @JsonProperty("idMapping")
    private List<IdMapping> idMapping;

    // Getters and setters
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Long getExpires() {
        return expires;
    }

    public void setExpires(Long expires) {
        this.expires = expires;
    }

    public String getBrokerUserId() {
        return brokerUserId;
    }

    public void setBrokerUserId(String brokerUserId) {
        this.brokerUserId = brokerUserId;
    }

    public String getTestCfg() {
        return testCfg;
    }

    public void setTestCfg(String testCfg) {
        this.testCfg = testCfg;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public LocalDate getSystemDate() {
        return systemDate;
    }

    public void setSystemDate(LocalDate systemDate) {
        this.systemDate = systemDate;
    }

    public List<IdMapping> getIdMapping() {
        return idMapping;
    }

    public void setIdMapping(List<IdMapping> idMapping) {
        this.idMapping = idMapping;
    }

    /**
     * Parse JSON string into PersonSessionToken object.
     */
    public static PersonSessionToken parse(String json) throws Exception {
        if (json == null || json.isBlank()) {
            throw new IllegalArgumentException("Token JSON cannot be null or blank");
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules(); // For LocalDate support
        return OBJECT_READER.readValue(TokenDecoderUtil.decodeIfNeeded(json), PersonSessionToken.class);

        // return mapper.readValue(json, PersonSessionToken.class);
    }
}
