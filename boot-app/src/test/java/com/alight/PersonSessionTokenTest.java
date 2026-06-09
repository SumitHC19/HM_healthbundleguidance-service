package com.alight;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.alight.asg.model.token.v1_0.IdMapping;
import com.alight.asg.model.token.v1_0.PersonSessionToken;

class PersonSessionTokenTest {

    @Test
    @DisplayName("parse(): binds valid JSON including LocalDate and idMapping")
    void parseValidJson() throws Exception {
        String json = "{"
                + "\"accessToken\":\"abc\","
                + "\"sessionId\":\"sid-1\","
                + "\"expires\":1700000000,"
                + "\"brokerUserId\":\"CBA\","
                + "\"testCfg\":\"t1\","
                + "\"locale\":\"en_US\","
                + "\"systemDate\":\"2024-12-31\","
                + "\"idMapping\":[{\"platformType\":\"CBA\",\"clientId\":\"19968\",\"platformInternalId\":\"12853765\"}]"
                + "}";

        PersonSessionToken token = PersonSessionToken.parse(json);

        assertThat(token).isNotNull();
        assertThat(token.getAccessToken()).isEqualTo("abc");
        assertThat(token.getSystemDate()).isEqualTo(LocalDate.of(2024, 12, 31));
        assertThat(token.getIdMapping()).hasSize(1);
        IdMapping mapping = token.getIdMapping().get(0);
        assertThat(mapping.getPlatformType()).isEqualTo("CBA");
        assertThat(mapping.getClientId()).isEqualTo("19968");
        assertThat(mapping.getPlatformInternalId()).isEqualTo("12853765");
    }

    @Test
    @DisplayName("parse(): ignores unknown fields")
    void parseIgnoresUnknownFields() throws Exception {
        String json = "{"
                + "\"someUnknown\":\"x\","
                + "\"another\":\"y\","
                + "\"locale\":\"en_US\""
                + "}";

        PersonSessionToken token = PersonSessionToken.parse(json);
        assertThat(token).isNotNull();
        assertThat(token.getLocale()).isEqualTo("en_US");
    }

    @Test
    @DisplayName("parse(): throws on invalid JSON")
    void parseInvalidJson() {
        String json = "{not valid json";
        assertThatThrownBy(() -> PersonSessionToken.parse(json))
                .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("parse(): throws IllegalArgumentException on null/blank")
    void parseNullOrBlank() {
        assertThatThrownBy(() -> PersonSessionToken.parse(null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> PersonSessionToken.parse("  "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("parse(): binds empty idMapping list")
    void parseEmptyIdMapping() throws Exception {
        String json = "{"
                + "\"idMapping\":[],"
                + "\"locale\":\"en_US\""
                + "}";
        PersonSessionToken token = PersonSessionToken.parse(json);
        assertThat(token.getIdMapping()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("parse(): handles null idMapping gracefully")
    void parseNullIdMapping() throws Exception {
        String json = "{"
                + "\"accessToken\":\"tok-1\","
                + "\"idMapping\":null"
                + "}";
        PersonSessionToken token = PersonSessionToken.parse(json);

        assertThat(token).isNotNull();
        assertThat(token.getAccessToken()).isEqualTo("tok-1");
        assertThat(token.getIdMapping()).isNull(); // explicit null stays null
    }

    @Test
    @DisplayName("parse(): minimal JSON with no fields results in non-null object with null properties")
    void parseMinimalJson() throws Exception {
        String json = "{}";
        PersonSessionToken token = PersonSessionToken.parse(json);

        assertThat(token).isNotNull();
        assertThat(token.getAccessToken()).isNull();
        assertThat(token.getSessionId()).isNull();
        assertThat(token.getExpires()).isNull();
        assertThat(token.getBrokerUserId()).isNull();
        assertThat(token.getTestCfg()).isNull();
        assertThat(token.getLocale()).isNull();
        assertThat(token.getSystemDate()).isNull();
        assertThat(token.getIdMapping()).isNull();
    }

    @Test
    @DisplayName("parse(): binds multiple idMapping entries")
    void parseMultipleIdMappings() throws Exception {
        String json = "{"
                + "\"idMapping\":["
                + "  {\"platformType\":\"ONE\",\"clientId\":\"11111\",\"platformInternalId\":\"AAA\"},"
                + "  {\"platformType\":\"TWO\",\"clientId\":\"22222\",\"platformInternalId\":\"BBB\"}"
                + "]"
                + "}";
        PersonSessionToken token = PersonSessionToken.parse(json);

        assertThat(token.getIdMapping()).isNotNull().hasSize(2);
        assertThat(token.getIdMapping().get(0).getPlatformType()).isEqualTo("ONE");
        assertThat(token.getIdMapping().get(1).getPlatformInternalId()).isEqualTo("BBB");
    }

    @Test
    @DisplayName("Getters/Setters: set all fields and verify getters")
    void gettersAndSetters_AllFields() {
        PersonSessionToken token = new PersonSessionToken();
        token.setAccessToken("acc");
        token.setSessionId("sid");
        token.setExpires(1234567890L);
        token.setBrokerUserId("CBA");
        token.setTestCfg("cfg");
        token.setLocale("en_US");
        token.setSystemDate(LocalDate.of(2025, 1, 5));

        // idMapping setters/getters via simple list
        IdMapping m1 = new IdMapping();
        m1.setPlatformType("CBA");
        m1.setClientId("19968");
        m1.setPlatformInternalId("12853765");

        IdMapping m2 = new IdMapping();
        m2.setPlatformType("OTHER");
        m2.setClientId("22222");
        m2.setPlatformInternalId("99999");

        token.setIdMapping(List.of(m1, m2));

        assertThat(token.getAccessToken()).isEqualTo("acc");
        assertThat(token.getSessionId()).isEqualTo("sid");
        assertThat(token.getExpires()).isEqualTo(1234567890L);
        assertThat(token.getBrokerUserId()).isEqualTo("CBA");
        assertThat(token.getTestCfg()).isEqualTo("cfg");
        assertThat(token.getLocale()).isEqualTo("en_US");
        assertThat(token.getSystemDate()).isEqualTo(LocalDate.of(2025, 1, 5));
        assertThat(token.getIdMapping()).hasSize(2);
        assertThat(token.getIdMapping().get(0).getClientId()).isEqualTo("19968");
        assertThat(token.getIdMapping().get(1).getPlatformInternalId()).isEqualTo("99999");
    }
}
