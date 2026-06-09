package com.alight;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.alight.asg.model.header.v1_0.RequestHeader;

class RequestHeaderTest {

    @Test
    @DisplayName("parse(): binds valid JSON fields")
    void parseValidJson() throws Exception {
        String json = "{"
                + "\"clientId\":\"19968\","
                + "\"systemInstanceId\":\"SYS1\","
                + "\"subjectId\":\"12853765\","
                + "\"subjectType\":\"MEMBER\","
                + "\"locale\":\"en_US\","
                + "\"roleId\":\"R1\","
                + "\"channelRequestData\":\"cd\","
                + "\"consumerReferenceId\":\"crid\""
                + "}";
        RequestHeader header = RequestHeader.parse(json);

        assertThat(header.getClientId()).isEqualTo("19968");
        assertThat(header.getSystemInstanceId()).isEqualTo("SYS1");
        assertThat(header.getSubjectId()).isEqualTo("12853765");
        assertThat(header.getSubjectType()).isEqualTo("MEMBER");
        assertThat(header.getLocale()).isEqualTo("en_US");
        assertThat(header.getRoleId()).isEqualTo("R1");
        assertThat(header.getChannelRequestData()).isEqualTo("cd");
        assertThat(header.getConsumerReferenceId()).isEqualTo("crid");
    }

    @Test
    @DisplayName("parse(): ignores unknown fields")
    void parseIgnoresUnknownFields() throws Exception {
        String json = "{"
                + "\"clientId\":\"19968\","
                + "\"unknown1\":\"x\","
                + "\"unknown2\":true"
                + "}";
        RequestHeader header = RequestHeader.parse(json);
        assertThat(header.getClientId()).isEqualTo("19968");
    }

    @Test
    @DisplayName("parse(): throws on invalid JSON")
    void parseInvalidJson() {
        assertThatThrownBy(() -> RequestHeader.parse("{bad json"))
                .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("parse(): throws IllegalArgumentException on null/blank")
    void parseNullOrBlank() {
        assertThatThrownBy(() -> RequestHeader.parse(null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> RequestHeader.parse("  "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("toJson(): round-trips through parse()")
    void toJsonRoundTrip() throws Exception {
        RequestHeader header = new RequestHeader();
        header.setClientId("19968");
        header.setSubjectId("12853765");
        header.setLocale("en_US");

        String json = header.toJson();
        RequestHeader parsed = RequestHeader.parse(json);

        assertThat(parsed.getClientId()).isEqualTo("19968");
        assertThat(parsed.getSubjectId()).isEqualTo("12853765");
        assertThat(parsed.getLocale()).isEqualTo("en_US");
    }
}
