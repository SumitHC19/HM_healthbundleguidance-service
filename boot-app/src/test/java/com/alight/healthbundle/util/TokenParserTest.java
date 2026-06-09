package com.alight.healthbundle.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

class TokenParserTest {

    private TokenParser tokenParser;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        tokenParser = new TokenParser(objectMapper);
    }

    // -----------------------------
    // extractPlatformInternalIdFromToken
    // -----------------------------

    @Test
    @DisplayName("Extract platformInternalId from valid JSON token with CBA platform type")
    void testExtractPlatformInternalIdFromToken_ValidJsonToken() {
        String token = "{\"brokerUserId\":\"CBA\",\"idMapping\":[{\"platformType\":\"CBA\",\"clientId\":\"19968\",\"platformInternalId\":\"12853765\"}],\"locale\":\"en_US\"}";

        String result = tokenParser.extractPlatformInternalIdFromToken(token);

        assertThat(result).isEqualTo("12853765");
    }

    @Test
    @DisplayName("Extract platformInternalId from Base64-encoded JSON token")
    void testExtractPlatformInternalIdFromToken_Base64Token() {
        String token = "{\"brokerUserId\":\"CBA\",\"idMapping\":[{\"platformType\":\"CBA\",\"clientId\":\"19968\",\"platformInternalId\":\"12853765\"}],\"locale\":\"en_US\"}";
        String encodedToken = Base64.getEncoder().encodeToString(token.getBytes());

        String result = tokenParser.extractPlatformInternalIdFromToken(encodedToken);

        assertThat(result).isEqualTo("12853765");
    }

    @Test
    @DisplayName("Extract platformInternalId from token with multiple idMapping entries (first non-blank wins)")
    void testExtractPlatformInternalIdFromToken_MultipleIdMappings() {
        String token = "{\"brokerUserId\":\"CBA\",\"idMapping\":["
                + "{\"platformType\":\"OTHER\",\"clientId\":\"11111\",\"platformInternalId\":\"99999\"},"
                + "{\"platformType\":\"CBA\",\"clientId\":\"19968\",\"platformInternalId\":\"12853765\"}"
                + "],\"locale\":\"en_US\"}";

        String result = tokenParser.extractPlatformInternalIdFromToken(token);

        // As per code, the first non-blank is used, regardless of platformType
        assertThat(result).isEqualTo("99999");
    }

    @Test
    @DisplayName("Extract platformInternalId returns null for null token")
    void testExtractPlatformInternalIdFromToken_NullToken() {
        String result = tokenParser.extractPlatformInternalIdFromToken(null);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Extract platformInternalId returns null for blank token")
    void testExtractPlatformInternalIdFromToken_BlankToken() {
        String result = tokenParser.extractPlatformInternalIdFromToken("   ");

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Extract platformInternalId returns null for empty token")
    void testExtractPlatformInternalIdFromToken_EmptyToken() {
        String result = tokenParser.extractPlatformInternalIdFromToken("");

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Extract platformInternalId from first idMapping entry regardless of platformType")
    void testExtractPlatformInternalIdFromToken_AnyPlatformType() {
        String token = "{\"brokerUserId\":\"CBA\",\"idMapping\":[{\"platformType\":\"OTHER\",\"clientId\":\"19968\",\"platformInternalId\":\"12853765\"}],\"locale\":\"en_US\"}";

        String result = tokenParser.extractPlatformInternalIdFromToken(token);

        assertThat(result).isEqualTo("12853765");
    }

    @Test
    @DisplayName("Extract platformInternalId returns null when idMapping is empty")
    void testExtractPlatformInternalIdFromToken_EmptyIdMapping() {
        String token = "{\"brokerUserId\":\"CBA\",\"idMapping\":[],\"locale\":\"en_US\"}";

        String result = tokenParser.extractPlatformInternalIdFromToken(token);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Extract platformInternalId returns null when idMapping is null")
    void testExtractPlatformInternalIdFromToken_NullIdMapping() {
        String token = "{\"brokerUserId\":\"CBA\",\"locale\":\"en_US\"}";

        String result = tokenParser.extractPlatformInternalIdFromToken(token);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Extract platformInternalId returns null when platformInternalId is blank")
    void testExtractPlatformInternalIdFromToken_BlankPlatformInternalId() {
        String token = "{\"brokerUserId\":\"CBA\",\"idMapping\":[{\"platformType\":\"CBA\",\"clientId\":\"19968\",\"platformInternalId\":\"   \"}],\"locale\":\"en_US\"}";

        String result = tokenParser.extractPlatformInternalIdFromToken(token);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Extract platformInternalId returns null when platformInternalId is null")
    void testExtractPlatformInternalIdFromToken_NullPlatformInternalId() {
        String token = "{\"brokerUserId\":\"CBA\",\"idMapping\":[{\"platformType\":\"CBA\",\"clientId\":\"19968\"}],\"locale\":\"en_US\"}";

        String result = tokenParser.extractPlatformInternalIdFromToken(token);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Extract platformInternalId returns null for invalid JSON")
    void testExtractPlatformInternalIdFromToken_InvalidJson() {
        String token = "{invalid json";

        String result = tokenParser.extractPlatformInternalIdFromToken(token);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Extract platformInternalId returns null when Base64 token decodes to printable non-JSON")
    void testExtractPlatformInternalIdFromToken_Base64DecodesToPrintableNonJson() {
        String notJson = "hello world";
        String encoded = Base64.getEncoder().encodeToString(notJson.getBytes(StandardCharsets.UTF_8));

        String result = tokenParser.extractPlatformInternalIdFromToken(encoded);

        // decodeTokenIfNeeded returns decoded "hello world", parse fails -> null
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Extract platformInternalId returns null when Base64 token decodes to binary/non-printable (treated as encrypted)")
    void testExtractPlatformInternalIdFromToken_Base64DecodesToBinaryEncryptedLike() {
        // Build bytes with non-printable characters (0x01 0x02 ...)
        byte[] binary = new byte[] { 0x01, 0x02, 0x03, 0x7F, 0x00, 0x10 };
        String encoded = Base64.getEncoder().encodeToString(binary);

        String result = tokenParser.extractPlatformInternalIdFromToken(encoded);

        // decodeTokenIfNeeded will detect non-printable -> return original token,
        // parse fails -> null
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Extract platformInternalId returns null when token is not Base64 and not JSON")
    void testExtractPlatformInternalIdFromToken_NotBase64AndNotJson() {
        // Decoding throws IllegalArgumentException -> returns original string "!!!",
        // then parse fails -> null
        String result = tokenParser.extractPlatformInternalIdFromToken("!!!");

        assertThat(result).isNull();
    }

    // -----------------------------
    // isTokenParsable
    // -----------------------------

    @Test
    @DisplayName("isTokenParsable returns true for valid JSON token")
    void testIsTokenParsable_ValidToken() {
        String token = "{\"brokerUserId\":\"CBA\",\"idMapping\":[{\"platformType\":\"CBA\",\"clientId\":\"19968\",\"platformInternalId\":\"12853765\"}],\"locale\":\"en_US\"}";

        boolean result = tokenParser.isTokenParsable(token);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isTokenParsable returns true for Base64-encoded valid JSON token")
    void testIsTokenParsable_Base64Token() {
        String token = "{\"brokerUserId\":\"CBA\",\"idMapping\":[{\"platformType\":\"CBA\",\"clientId\":\"19968\",\"platformInternalId\":\"12853765\"}],\"locale\":\"en_US\"}";
        String encodedToken = Base64.getEncoder().encodeToString(token.getBytes());

        boolean result = tokenParser.isTokenParsable(encodedToken);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isTokenParsable returns false for null token")
    void testIsTokenParsable_NullToken() {
        boolean result = tokenParser.isTokenParsable(null);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isTokenParsable returns false for blank token")
    void testIsTokenParsable_BlankToken() {
        boolean result = tokenParser.isTokenParsable("   ");

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isTokenParsable returns false for empty token")
    void testIsTokenParsable_EmptyToken() {
        boolean result = tokenParser.isTokenParsable("");

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isTokenParsable returns false for invalid JSON")
    void testIsTokenParsable_InvalidJson() {
        String token = "{invalid json}";

        boolean result = tokenParser.isTokenParsable(token);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isTokenParsable returns false for non-Base64 token that is not JSON")
    void testIsTokenParsable_NotBase64AndNotJson() {
        boolean result = tokenParser.isTokenParsable("not-base64!");

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isTokenParsable returns false when Base64 decodes to printable non-JSON")
    void testIsTokenParsable_Base64PrintableNonJson() {
        String notJson = "hello";
        String encoded = Base64.getEncoder().encodeToString(notJson.getBytes(StandardCharsets.UTF_8));

        boolean result = tokenParser.isTokenParsable(encoded);

        assertThat(result).isFalse();
    }

    // -----------------------------
    // extractClientIdFromRequestHeader
    // -----------------------------

    @Test
    @DisplayName("Extract clientId from valid request header")
    void testExtractClientIdFromRequestHeader_ValidHeader() {
        String requestHeader = "{\"clientId\":\"19968\",\"subjectId\":\"12853765\",\"subjectType\":\"MEMBER\"}";

        String result = tokenParser.extractClientIdFromRequestHeader(requestHeader);

        assertThat(result).isEqualTo("19968");
    }

    @Test
    @DisplayName("Extract clientId returns null for null request header")
    void testExtractClientIdFromRequestHeader_NullHeader() {
        String result = tokenParser.extractClientIdFromRequestHeader(null);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Extract clientId returns null for empty request header")
    void testExtractClientIdFromRequestHeader_EmptyHeader() {
        String result = tokenParser.extractClientIdFromRequestHeader("");

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Extract clientId returns null when clientId is missing")
    void testExtractClientIdFromRequestHeader_MissingClientId() {
        String requestHeader = "{\"subjectId\":\"12853765\",\"subjectType\":\"MEMBER\"}";

        String result = tokenParser.extractClientIdFromRequestHeader(requestHeader);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Extract clientId returns null when clientId is blank")
    void testExtractClientIdFromRequestHeader_BlankClientId() {
        String requestHeader = "{\"clientId\":\"   \",\"subjectId\":\"12853765\"}";

        String result = tokenParser.extractClientIdFromRequestHeader(requestHeader);

        assertThat(result).isNull();
    }

    // -----------------------------
    // extractPlatformInternalIdFromRequestHeader
    // -----------------------------

    @Test
    @DisplayName("Extract platformInternalId from request header using subjectId")
    void testExtractPlatformInternalIdFromRequestHeader_ValidHeader() {
        String requestHeader = "{\"clientId\":\"19968\",\"subjectId\":\"12853765\",\"subjectType\":\"MEMBER\"}";

        String result = tokenParser.extractPlatformInternalIdFromRequestHeader(requestHeader);

        assertThat(result).isEqualTo("12853765");
    }

    @Test
    @DisplayName("Extract platformInternalId from request header returns null for null header")
    void testExtractPlatformInternalIdFromRequestHeader_NullHeader() {
        String result = tokenParser.extractPlatformInternalIdFromRequestHeader(null);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Extract platformInternalId from request header falls back to plain value")
    void testExtractPlatformInternalIdFromRequestHeader_PlainValue() {
        String requestHeader = "12853765";

        String result = tokenParser.extractPlatformInternalIdFromRequestHeader(requestHeader);

        assertThat(result).isEqualTo("12853765");
    }

    @Test
    @DisplayName("Extract platformInternalId from request header returns null when subjectId is missing")
    void testExtractPlatformInternalIdFromRequestHeader_MissingSubjectId() {
        String requestHeader = "{\"clientId\":\"19968\",\"subjectType\":\"MEMBER\"}";

        String result = tokenParser.extractPlatformInternalIdFromRequestHeader(requestHeader);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Extract platformInternalId from request header returns null when subjectId is blank")
    void testExtractPlatformInternalIdFromRequestHeader_BlankSubjectId() {
        String requestHeader = "{\"clientId\":\"19968\",\"subjectId\":\"   \",\"subjectType\":\"MEMBER\"}";

        String result = tokenParser.extractPlatformInternalIdFromRequestHeader(requestHeader);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Extract platformInternalId from request header returns trimmed fallback when JSON parse fails but header is non-empty")
    void testExtractPlatformInternalIdFromRequestHeader_InvalidJsonReturnsTrimmed() {
        String header = "   77777   "; // invalid JSON, non-empty after trim

        String result = tokenParser.extractPlatformInternalIdFromRequestHeader(header);

        // As per fallback branch in catch: return trimmed if non-empty
        assertThat(result).isEqualTo("77777");
    }
}
