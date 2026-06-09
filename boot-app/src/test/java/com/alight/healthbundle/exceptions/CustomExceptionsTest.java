package com.alight.healthbundle.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for all custom exceptions.
 * Tests exception creation, messages, and HTTP status codes.
 */
@DisplayName("Custom Exceptions Tests")
class CustomExceptionsTest {

    @Test
    @DisplayName("BadRequestException should have correct status")
    void testBadRequestException() {
        // Act
        BadRequestException ex = new BadRequestException("Invalid input");

        // Assert
        assertNotNull(ex);
        assertEquals("Invalid input", ex.getMessage());
    }

    @Test
    @DisplayName("BadRequestException with cause")
    void testBadRequestExceptionWithCause() {
        // Arrange
        Throwable cause = new RuntimeException("Root cause");

        // Act
        BadRequestException ex = new BadRequestException("Invalid input", cause);

        // Assert
        assertEquals("Invalid input", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

    @Test
    @DisplayName("UnauthorizedException should indicate 401 status")
    void testUnauthorizedException() {
        // Act
        UnauthorizedException ex = new UnauthorizedException("Not authorized");

        // Assert
        assertNotNull(ex);
        assertEquals("Not authorized", ex.getMessage());
    }

    @Test
    @DisplayName("ForbiddenException should indicate 403 status")
    void testForbiddenException() {
        // Act
        ForbiddenException ex = new ForbiddenException("Access forbidden");

        // Assert
        assertNotNull(ex);
        assertEquals("Access forbidden", ex.getMessage());
    }

    @Test
    @DisplayName("NoObjectFoundException should indicate 404 status")
    void testNoObjectFoundException() {
        // Act
        NoObjectFoundException ex = new NoObjectFoundException("Object not found");

        // Assert
        assertNotNull(ex);
        assertEquals("Object not found", ex.getMessage());
    }

    @Test
    @DisplayName("VersionConflictException should indicate 409 status")
    void testVersionConflictException() {
        // Act
        VersionConflictException ex = new VersionConflictException("Version conflict");

        // Assert
        assertNotNull(ex);
        assertEquals("Version conflict", ex.getMessage());
    }

    @Test
    @DisplayName("Exception with null message")
    void testExceptionWithNullMessage() {
        // Act
        BadRequestException ex = new BadRequestException((String) null);

        // Assert
        assertNull(ex.getMessage());
    }

    @Test
    @DisplayName("Exception with empty message")
    void testExceptionWithEmptyMessage() {
        // Act
        BadRequestException ex = new BadRequestException("");

        // Assert
        assertEquals("", ex.getMessage());
    }

    @Test
    @DisplayName("Multiple exceptions can be thrown")
    void testMultipleExceptions() {
        // Act & Assert
        assertThrows(BadRequestException.class, () -> {
            throw new BadRequestException("Bad request");
        });

        assertThrows(UnauthorizedException.class, () -> {
            throw new UnauthorizedException("Unauthorized");
        });

        assertThrows(ForbiddenException.class, () -> {
            throw new ForbiddenException("Forbidden");
        });

        assertThrows(NoObjectFoundException.class, () -> {
            throw new NoObjectFoundException("Not found");
        });

        assertThrows(VersionConflictException.class, () -> {
            throw new VersionConflictException("Conflict");
        });
    }

    @Test
    @DisplayName("Exception with special characters in message")
    void testExceptionWithSpecialCharacters() {
        // Act
        String message = "Error: @#$%^&*()_+-={}[]|:;<>?,./";
        BadRequestException ex = new BadRequestException(message);

        // Assert
        assertEquals(message, ex.getMessage());
    }

    @Test
    @DisplayName("Exception with numeric message")
    void testExceptionWithNumericMessage() {
        // Act
        BadRequestException ex = new BadRequestException("Error code: 12345");

        // Assert
        assertEquals("Error code: 12345", ex.getMessage());
    }

    @Test
    @DisplayName("Exception with long message")
    void testExceptionWithLongMessage() {
        // Arrange
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("Error message line ").append(i).append(". ");
        }
        String longMessage = sb.toString();

        // Act
        BadRequestException ex = new BadRequestException(longMessage);

        // Assert
        assertEquals(longMessage, ex.getMessage());
        assertTrue(ex.getMessage().length() > 10000);
    }

    @Test
    @DisplayName("Exception inheritance chain")
    void testExceptionInheritance() {
        // Act
        BadRequestException ex = new BadRequestException("Test");

        // Assert
        assertInstanceOf(Exception.class, ex);
        assertInstanceOf(RuntimeException.class, ex);
    }

    @Test
    @DisplayName("Cannot instantiate exception with null cause explicitly")
    void testExceptionCauseHandling() {
        // Act
        BadRequestException ex = new BadRequestException("Message", null);

        // Assert
        assertNull(ex.getCause());
    }
}
