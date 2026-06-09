package com.alight.asg.model.token.v1_0;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Vendored IdMapping from Alight ASG model library.
 * Represents an entry in the idMapping array of PersonSessionToken.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IdMapping {

    @JsonProperty("roleType")
    private String roleType;

    @JsonProperty("platformType")
    private String platformType;

    @JsonProperty("clientId")
    private String clientId;

    @JsonProperty("normalizedClientid")
    private String normalizedClientid;

    @JsonProperty("systemInstanceId")
    private String systemInstanceId;

    @JsonProperty("platformInternalId")
    private String platformInternalId;

    @JsonProperty("platformExternalId")
    private String platformExternalId;

    @JsonProperty("sourceSchemaName")
    private String sourceSchemaName;

    @JsonProperty("domain")
    private String domain;

    // Getters and setters
    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public String getPlatformType() {
        return platformType;
    }

    public void setPlatformType(String platformType) {
        this.platformType = platformType;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getNormalizedClientid() {
        return normalizedClientid;
    }

    public void setNormalizedClientid(String normalizedClientid) {
        this.normalizedClientid = normalizedClientid;
    }

    public String getSystemInstanceId() {
        return systemInstanceId;
    }

    public void setSystemInstanceId(String systemInstanceId) {
        this.systemInstanceId = systemInstanceId;
    }

    public String getPlatformInternalId() {
        return platformInternalId;
    }

    public void setPlatformInternalId(String platformInternalId) {
        this.platformInternalId = platformInternalId;
    }

    public String getPlatformExternalId() {
        return platformExternalId;
    }

    public void setPlatformExternalId(String platformExternalId) {
        this.platformExternalId = platformExternalId;
    }

    public String getSourceSchemaName() {
        return sourceSchemaName;
    }

    public void setSourceSchemaName(String sourceSchemaName) {
        this.sourceSchemaName = sourceSchemaName;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
