package com.alight;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.alight.asg.model.token.v1_0.IdMapping;
import com.fasterxml.jackson.databind.ObjectMapper;

class IdMappingTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("Getters and Setters: set all fields then verify via getters")
    void gettersAndSetters_allFields() {
        IdMapping m = new IdMapping();
        m.setRoleType("ROLE");
        m.setPlatformType("CBA");
        m.setClientId("19968");
        m.setNormalizedClientid("19968-NORM");
        m.setSystemInstanceId("SYS1");
        m.setPlatformInternalId("12853765");
        m.setPlatformExternalId("EXT-999");
        m.setSourceSchemaName("SCHEMA1");
        m.setDomain("HEALTH");

        assertThat(m.getRoleType()).isEqualTo("ROLE");
        assertThat(m.getPlatformType()).isEqualTo("CBA");
        assertThat(m.getClientId()).isEqualTo("19968");
        assertThat(m.getNormalizedClientid()).isEqualTo("19968-NORM");
        assertThat(m.getSystemInstanceId()).isEqualTo("SYS1");
        assertThat(m.getPlatformInternalId()).isEqualTo("12853765");
        assertThat(m.getPlatformExternalId()).isEqualTo("EXT-999");
        assertThat(m.getSourceSchemaName()).isEqualTo("SCHEMA1");
        assertThat(m.getDomain()).isEqualTo("HEALTH");
    }

    @Test
    @DisplayName("Jackson: deserialize all known fields from JSON")
    void jackson_deserializeAllKnownFields() throws Exception {
        String json = "{"
                + "\"roleType\":\"ROLE\","
                + "\"platformType\":\"CBA\","
                + "\"clientId\":\"19968\","
                + "\"normalizedClientid\":\"19968-NORM\","
                + "\"systemInstanceId\":\"SYS1\","
                + "\"platformInternalId\":\"12853765\","
                + "\"platformExternalId\":\"EXT-999\","
                + "\"sourceSchemaName\":\"SCHEMA1\","
                + "\"domain\":\"HEALTH\""
                + "}";

        IdMapping m = mapper.readValue(json, IdMapping.class);

        assertThat(m.getRoleType()).isEqualTo("ROLE");
        assertThat(m.getPlatformType()).isEqualTo("CBA");
        assertThat(m.getClientId()).isEqualTo("19968");
        assertThat(m.getNormalizedClientid()).isEqualTo("19968-NORM");
        assertThat(m.getSystemInstanceId()).isEqualTo("SYS1");
        assertThat(m.getPlatformInternalId()).isEqualTo("12853765");
        assertThat(m.getPlatformExternalId()).isEqualTo("EXT-999");
        assertThat(m.getSourceSchemaName()).isEqualTo("SCHEMA1");
        assertThat(m.getDomain()).isEqualTo("HEALTH");
    }

    @Test
    @DisplayName("Jackson: serialize then deserialize should round-trip values")
    void jackson_roundTrip() throws Exception {
        IdMapping original = new IdMapping();
        original.setRoleType("ROLE");
        original.setPlatformType("CBA");
        original.setClientId("19968");
        original.setNormalizedClientid("19968-NORM");
        original.setSystemInstanceId("SYS1");
        original.setPlatformInternalId("12853765");
        original.setPlatformExternalId("EXT-999");
        original.setSourceSchemaName("SCHEMA1");
        original.setDomain("HEALTH");

        String json = mapper.writeValueAsString(original);
        IdMapping roundTripped = mapper.readValue(json, IdMapping.class);

        assertThat(roundTripped.getRoleType()).isEqualTo("ROLE");
        assertThat(roundTripped.getPlatformType()).isEqualTo("CBA");
        assertThat(roundTripped.getClientId()).isEqualTo("19968");
        assertThat(roundTripped.getNormalizedClientid()).isEqualTo("19968-NORM");
        assertThat(roundTripped.getSystemInstanceId()).isEqualTo("SYS1");
        assertThat(roundTripped.getPlatformInternalId()).isEqualTo("12853765");
        assertThat(roundTripped.getPlatformExternalId()).isEqualTo("EXT-999");
        assertThat(roundTripped.getSourceSchemaName()).isEqualTo("SCHEMA1");
        assertThat(roundTripped.getDomain()).isEqualTo("HEALTH");
    }

    @Test
    @DisplayName("Jackson: ignore unknown fields due to @JsonIgnoreProperties")
    void jackson_ignoreUnknownFields() throws Exception {
        String json = "{"
                + "\"platformType\":\"CBA\","
                + "\"clientId\":\"19968\","
                + "\"platformInternalId\":\"12853765\","
                + "\"unknownField\":\"ignored\","
                + "\"anotherUnknown\":42"
                + "}";

        IdMapping mapping = mapper.readValue(json, IdMapping.class);

        assertThat(mapping.getPlatformType()).isEqualTo("CBA");
        assertThat(mapping.getClientId()).isEqualTo("19968");
        assertThat(mapping.getPlatformInternalId()).isEqualTo("12853765");
        // No exception due to unknown fields
    }

    @Test
    @DisplayName("Jackson: partial fields and null values are handled safely")
    void jackson_partialAndNulls() throws Exception {
        String json = "{"
                + "\"clientId\":\"19968\","
                + "\"platformInternalId\":null,"
                + "\"domain\":null"
                + "}";

        IdMapping m = mapper.readValue(json, IdMapping.class);

        assertThat(m.getClientId()).isEqualTo("19968");
        assertThat(m.getPlatformInternalId()).isNull();
        assertThat(m.getDomain()).isNull();

        // Setters accept nulls
        m.setClientId(null);
        m.setPlatformType(null);
        m.setNormalizedClientid(null);
        m.setSystemInstanceId(null);
        m.setPlatformExternalId(null);
        m.setSourceSchemaName(null);

        assertThat(m.getClientId()).isNull();
        assertThat(m.getPlatformType()).isNull();
        assertThat(m.getNormalizedClientid()).isNull();
        assertThat(m.getSystemInstanceId()).isNull();
        assertThat(m.getPlatformExternalId()).isNull();
        assertThat(m.getSourceSchemaName()).isNull();
    }

    @Test
    @DisplayName("IdMapping: default constructor creates all-null fields")
    void defaultConstructor_allNull() {
        IdMapping m = new IdMapping();
        assertThat(m.getRoleType()).isNull();
        assertThat(m.getPlatformType()).isNull();
        assertThat(m.getClientId()).isNull();
        assertThat(m.getNormalizedClientid()).isNull();
        assertThat(m.getSystemInstanceId()).isNull();
        assertThat(m.getPlatformInternalId()).isNull();
        assertThat(m.getPlatformExternalId()).isNull();
        assertThat(m.getSourceSchemaName()).isNull();
        assertThat(m.getDomain()).isNull();
    }
}
