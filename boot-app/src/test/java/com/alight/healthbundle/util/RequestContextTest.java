package com.alight.healthbundle.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RequestContext utility class.
 * Tests the request context data holder and utility methods.
 */
@DisplayName("RequestContext Tests")
class RequestContextTest {

    private static final String CLIENT_ID = "CLIENT-001";
    private static final String PLATFORM_ID = "PLATFORM-001";

    @Test
    @DisplayName("Should create RequestContext with clientId and platformInternalId")
    void testConstructor() {
        // Act
        RequestContext context = new RequestContext(CLIENT_ID, PLATFORM_ID);

        // Assert
        assertNotNull(context);
        assertEquals(CLIENT_ID, context.getClientId());
        assertEquals(PLATFORM_ID, context.getPlatformInternalId());
    }

    @Test
    @DisplayName("Should get clientId correctly")
    void testGetClientId() {
        // Arrange
        RequestContext context = new RequestContext(CLIENT_ID, PLATFORM_ID);

        // Act
        String result = context.getClientId();

        // Assert
        assertEquals(CLIENT_ID, result);
    }

    @Test
    @DisplayName("Should get platformInternalId correctly")
    void testGetPlatformInternalId() {
        // Arrange
        RequestContext context = new RequestContext(CLIENT_ID, PLATFORM_ID);

        // Act
        String result = context.getPlatformInternalId();

        // Assert
        assertEquals(PLATFORM_ID, result);
    }

    @Test
    @DisplayName("Should set clientId")
    void testSetClientId() {
        // Arrange
        RequestContext context = new RequestContext(CLIENT_ID, PLATFORM_ID);
        String newClientId = "NEW-CLIENT";

        // Act
        context.setClientId(newClientId);

        // Assert
        assertEquals(newClientId, context.getClientId());
    }

    @Test
    @DisplayName("Should set platformInternalId")
    void testSetPlatformInternalId() {
        // Arrange
        RequestContext context = new RequestContext(CLIENT_ID, PLATFORM_ID);
        String newPlatformId = "NEW-PLATFORM";

        // Act
        context.setPlatformInternalId(newPlatformId);

        // Assert
        assertEquals(newPlatformId, context.getPlatformInternalId());
    }

    @Test
    @DisplayName("Should handle null clientId")
    void testNullClientId() {
        // Act
        RequestContext context = new RequestContext(null, PLATFORM_ID);

        // Assert
        assertNull(context.getClientId());
    }

    @Test
    @DisplayName("Should handle null platformInternalId")
    void testNullPlatformId() {
        // Act
        RequestContext context = new RequestContext(CLIENT_ID, null);

        // Assert
        assertNull(context.getPlatformInternalId());
    }

    @Test
    @DisplayName("Should handle both null values")
    void testBothNull() {
        // Act
        RequestContext context = new RequestContext(null, null);

        // Assert
        assertNull(context.getClientId());
        assertNull(context.getPlatformInternalId());
    }

    @Test
    @DisplayName("Should update both values independently")
    void testIndependentUpdates() {
        // Arrange
        RequestContext context = new RequestContext("OLD-CLIENT", "OLD-PLATFORM");

        // Act
        context.setClientId("NEW-CLIENT");
        context.setPlatformInternalId("NEW-PLATFORM");

        // Assert
        assertEquals("NEW-CLIENT", context.getClientId());
        assertEquals("NEW-PLATFORM", context.getPlatformInternalId());
    }

    @Test
    @DisplayName("Should handle special characters in values")
    void testSpecialCharacters() {
        // Arrange
        String clientId = "CLIENT-#@!$%";
        String platformId = "PLATFORM-_-*";

        // Act
        RequestContext context = new RequestContext(clientId, platformId);

        // Assert
        assertEquals(clientId, context.getClientId());
        assertEquals(platformId, context.getPlatformInternalId());
    }

    @Test
    @DisplayName("Should handle empty strings")
    void testEmptyStrings() {
        // Act
        RequestContext context = new RequestContext("", "");

        // Assert
        assertEquals("", context.getClientId());
        assertEquals("", context.getPlatformInternalId());
    }
}
