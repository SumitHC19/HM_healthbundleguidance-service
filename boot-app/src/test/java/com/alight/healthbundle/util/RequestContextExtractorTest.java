package com.alight.healthbundle.util;

import com.alight.healthbundle.exceptions.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link RequestContextExtractor}.
 *
 * Coverage goals:
 * - All branches for clientId extraction & validation
 * - Token parsable vs not parsable paths
 * - Fallback to platformInternalIdHeader
 * - Error cases when required values are missing
 */
class RequestContextExtractorTest {

    private TokenParser tokenParser;
    private RequestContextExtractor extractor;

    @BeforeEach
    void setUp() {
        tokenParser = Mockito.mock(TokenParser.class);
        extractor = new RequestContextExtractor(tokenParser);
    }

    @Nested
    @DisplayName("Happy path scenarios")
    class HappyPath {

        @Test
        @DisplayName("Token parsable → platformInternalId from token; clientId from header")
        void extractContext_tokenParsable_useTokenForPlatformInternalId() {
            // Arrange
            String token = "valid.jwt.token";
            String requestHeader = "{\"clientId\":\"01234\"}";
            String platformInternalIdHeader = "HEADER_696000061";

            when(tokenParser.extractClientIdFromRequestHeader(requestHeader)).thenReturn("01234");
            when(tokenParser.isTokenParsable(token)).thenReturn(true);
            when(tokenParser.extractPlatformInternalIdFromToken(token)).thenReturn("PLATFORM_TOKEN_999");

            // Act
            RequestContext ctx = extractor.extractRequestContext(token, requestHeader, platformInternalIdHeader);

            // Assert
            assertNotNull(ctx);
            assertEquals("01234", ctx.getClientId());
            assertEquals("PLATFORM_TOKEN_999", ctx.getPlatformInternalId());

            verify(tokenParser).extractClientIdFromRequestHeader(requestHeader);
            verify(tokenParser).isTokenParsable(token);
            verify(tokenParser).extractPlatformInternalIdFromToken(token);
            verifyNoMoreInteractions(tokenParser);
        }

        @Test
        @DisplayName("Token NOT parsable → fallback to platformInternalId header")
        void extractContext_tokenNotParsable_useHeaderForPlatformInternalId() {
            // Arrange
            String token = "not_parsable";
            String requestHeader = "{\"clientId\":\"01234\"}";
            String platformInternalIdHeader = "HEADER_696000061";

            when(tokenParser.extractClientIdFromRequestHeader(requestHeader)).thenReturn("01234");
            when(tokenParser.isTokenParsable(token)).thenReturn(false);

            // Act
            RequestContext ctx = extractor.extractRequestContext(token, requestHeader, platformInternalIdHeader);

            // Assert
            assertEquals("01234", ctx.getClientId());
            assertEquals("HEADER_696000061", ctx.getPlatformInternalId());

            verify(tokenParser).extractClientIdFromRequestHeader(requestHeader);
            verify(tokenParser).isTokenParsable(token);
            verifyNoMoreInteractions(tokenParser);
        }

        @Test
        @DisplayName("Token parsable but returns BLANK → fallback to header still succeeds")
        void extractContext_tokenParsableButBlank_useHeaderForPlatformInternalId() {
            // Arrange
            String token = "valid.jwt.token";
            String requestHeader = "{\"clientId\":\"01234\"}";
            String platformInternalIdHeader = "HEADER_696000061";

            when(tokenParser.extractClientIdFromRequestHeader(requestHeader)).thenReturn("01234");
            when(tokenParser.isTokenParsable(token)).thenReturn(true);
            when(tokenParser.extractPlatformInternalIdFromToken(token)).thenReturn("  "); // blank from token

            // Act
            RequestContext ctx = extractor.extractRequestContext(token, requestHeader, platformInternalIdHeader);

            // Assert
            assertEquals("01234", ctx.getClientId());
            assertEquals("HEADER_696000061", ctx.getPlatformInternalId());

            verify(tokenParser).extractClientIdFromRequestHeader(requestHeader);
            verify(tokenParser).isTokenParsable(token);
            verify(tokenParser).extractPlatformInternalIdFromToken(token);
            verifyNoMoreInteractions(tokenParser);
        }

        @Test
        @DisplayName("Null token → treat as not parsable, use header for platformInternalId")
        void extractContext_nullToken_useHeaderForPlatformInternalId() {
            // Arrange
            String token = null;
            String requestHeader = "{\"clientId\":\"01234\"}";
            String platformInternalIdHeader = "HEADER_696000061";

            when(tokenParser.extractClientIdFromRequestHeader(requestHeader)).thenReturn("01234");
            when(tokenParser.isTokenParsable(token)).thenReturn(false);

            // Act
            RequestContext ctx = extractor.extractRequestContext(token, requestHeader, platformInternalIdHeader);

            // Assert
            assertEquals("01234", ctx.getClientId());
            assertEquals("HEADER_696000061", ctx.getPlatformInternalId());

            verify(tokenParser).extractClientIdFromRequestHeader(requestHeader);
            verify(tokenParser).isTokenParsable(token);
            verifyNoMoreInteractions(tokenParser);
        }
    }

    @Nested
    @DisplayName("Error scenarios")
    class Errors {

        @Test
        @DisplayName("Missing clientId in header → BadRequestException")
        void extractContext_missingClientId_throwsBadRequest() {
            // Arrange
            String token = "anything";
            String requestHeader = "{\"missingClientId\":true}";
            String platformInternalIdHeader = "HEADER_696000061";

            when(tokenParser.extractClientIdFromRequestHeader(requestHeader)).thenReturn(null);

            // Act + Assert
            BadRequestException ex = assertThrows(
                    BadRequestException.class,
                    () -> extractor.extractRequestContext(token, requestHeader, platformInternalIdHeader));
            assertTrue(ex.getMessage().toLowerCase().contains("client id is required"));

            verify(tokenParser).extractClientIdFromRequestHeader(requestHeader);
            verifyNoMoreInteractions(tokenParser);
        }

        @Test
        @DisplayName("Blank clientId in header → BadRequestException")
        void extractContext_blankClientId_throwsBadRequest() {
            // Arrange
            String token = "anything";
            String requestHeader = "{\"clientId\":\"   \"}";
            String platformInternalIdHeader = "HEADER_696000061";

            when(tokenParser.extractClientIdFromRequestHeader(requestHeader)).thenReturn("   ");

            // Act + Assert
            BadRequestException ex = assertThrows(
                    BadRequestException.class,
                    () -> extractor.extractRequestContext(token, requestHeader, platformInternalIdHeader));
            assertTrue(ex.getMessage().toLowerCase().contains("client id is required"));

            verify(tokenParser).extractClientIdFromRequestHeader(requestHeader);
            verifyNoMoreInteractions(tokenParser);
        }

        @Test
        @DisplayName("PlatformInternalId missing in both token and header → BadRequestException")
        void extractContext_missingPlatformInternalId_throwsBadRequest() {
            // Arrange
            String token = "valid.jwt.token";
            String requestHeader = "{\"clientId\":\"01234\"}";
            String platformInternalIdHeader = "   "; // blank

            when(tokenParser.extractClientIdFromRequestHeader(requestHeader)).thenReturn("01234");
            when(tokenParser.isTokenParsable(token)).thenReturn(true);
            when(tokenParser.extractPlatformInternalIdFromToken(token)).thenReturn(""); // blank in token too

            // Act + Assert
            BadRequestException ex = assertThrows(
                    BadRequestException.class,
                    () -> extractor.extractRequestContext(token, requestHeader, platformInternalIdHeader));
            assertTrue(ex.getMessage().toLowerCase().contains("platform internal id is required"));

            verify(tokenParser).extractClientIdFromRequestHeader(requestHeader);
            verify(tokenParser).isTokenParsable(token);
            verify(tokenParser).extractPlatformInternalIdFromToken(token);
            verifyNoMoreInteractions(tokenParser);
        }
    }
}
