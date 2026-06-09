package com.alight.healthbundle;

import static org.junit.jupiter.api.Assertions.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import com.alight.asg.model.header.v1_0.TokenDecoderUtil;

class DecodeIfNeededTest {

    /**
     * Assumptions for tests (do NOT import or reflect production constants):
     * - The method should decode when the string starts with URL-encoded braces,
     * i.e., "%7B" for '{' or "%7D" for '}'.
     * - Otherwise, it should return the original string unchanged.
     */

    @Test
    void returnsNullWhenInputIsNull() throws Exception {
        String result = TokenDecoderUtil.decodeIfNeeded(null);
        assertNull(result);
    }

    @Test
    void returnsSameStringWhenNoMarkerAtStart() throws Exception {
        String input = "plain-json";
        String result = TokenDecoderUtil.decodeIfNeeded(input);
        assertSame(input, result, "Should return as-is if not starting with encoded markers");
    }

    @Test
    void decodesWhenStartsWithEncodedOpenBrace() throws Exception {
        // Build an input that starts with "%7B" (encoded '{')
        String suffix = "\"k\":\"v\"}";
        // URLEncoder encodes '{' to %7B and '}' to %7D with UTF-8; we only need the
        // first char to be %7B
        String input = "%7B" + suffix; // "%7B\"k\":\"v\"}"

        String decoded = TokenDecoderUtil.decodeIfNeeded(input);
        assertEquals("{" + suffix, decoded);
    }

    @Test
    void idempotentWhenAlreadyPlainAndStartsWithBraceButNotEncoded() throws Exception {
        String input = "{alreadyPlain}";
        String result = TokenDecoderUtil.decodeIfNeeded(input);
        assertSame(input, result, "Should NOT decode when it's a plain string without encoded prefix");
    }

    // Optional: prove that a fully encoded JSON is handled too
    @Test
    void fullyEncodedJsonIsDecoded() throws Exception {
        String json = "{\"k\":\"v\"}";
        String fullyEncoded = URLEncoder.encode(json, StandardCharsets.UTF_8.name()); // %7B%22k%22%3A%22v%22%7D

        String decoded = TokenDecoderUtil.decodeIfNeeded(fullyEncoded);
        // Because decodeIfNeeded only checks the prefix, this is decoded exactly once
        // here
        assertEquals(json, decoded);
    }
}
