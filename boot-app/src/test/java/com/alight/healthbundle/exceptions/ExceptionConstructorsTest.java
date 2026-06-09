package com.alight.healthbundle.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Exception Constructors and Annotations Tests")
class ExceptionConstructorsTest {

    @Test
    @DisplayName("BadRequestException constructors and ResponseStatus annotation")
    void testBadRequestExceptionConstructorsAndAnnotation() {
        BadRequestException noArg = new BadRequestException();
        assertNotNull(noArg);

        BadRequestException withMessage = new BadRequestException("bad");
        assertEquals("bad", withMessage.getMessage());

        Throwable cause = new IllegalStateException("cause");
        BadRequestException withCause = new BadRequestException(cause);
        assertEquals(cause, withCause.getCause());

        BadRequestException withBoth = new BadRequestException("msg", cause);
        assertEquals("msg", withBoth.getMessage());
        assertEquals(cause, withBoth.getCause());

        ResponseStatus ann = BadRequestException.class.getAnnotation(ResponseStatus.class);
        assertNotNull(ann, "BadRequestException must be annotated with @ResponseStatus");
        assertEquals(HttpStatus.BAD_REQUEST, ann.value());
    }

    @Test
    @DisplayName("UnauthorizedException constructors")
    void testUnauthorizedExceptionConstructors() {
        UnauthorizedException noArg = new UnauthorizedException();
        assertNotNull(noArg);

        UnauthorizedException withMessage = new UnauthorizedException("unauth");
        assertEquals("unauth", withMessage.getMessage());

        Throwable cause = new RuntimeException("c");
        UnauthorizedException withCause = new UnauthorizedException(cause);
        assertEquals(cause, withCause.getCause());

        UnauthorizedException withBoth = new UnauthorizedException("m", cause);
        assertEquals("m", withBoth.getMessage());
        assertEquals(cause, withBoth.getCause());
    }

    @Test
    @DisplayName("ForbiddenException constructors")
    void testForbiddenExceptionConstructors() {
        ForbiddenException noArg = new ForbiddenException();
        assertNotNull(noArg);

        ForbiddenException withMessage = new ForbiddenException("forbid");
        assertEquals("forbid", withMessage.getMessage());

        Throwable cause = new RuntimeException("cause");
        ForbiddenException withCause = new ForbiddenException(cause);
        assertEquals(cause, withCause.getCause());

        ForbiddenException withBoth = new ForbiddenException("m", cause);
        assertEquals("m", withBoth.getMessage());
        assertEquals(cause, withBoth.getCause());
    }

    @Test
    @DisplayName("NoObjectFoundException constructors")
    void testNoObjectFoundExceptionConstructors() {
        NoObjectFoundException noArg = new NoObjectFoundException();
        assertNotNull(noArg);

        NoObjectFoundException withMessage = new NoObjectFoundException("notfound");
        assertEquals("notfound", withMessage.getMessage());

        Throwable cause = new RuntimeException("cause");
        NoObjectFoundException withCause = new NoObjectFoundException(cause);
        assertEquals(cause, withCause.getCause());

        NoObjectFoundException withBoth = new NoObjectFoundException("m", cause);
        assertEquals("m", withBoth.getMessage());
        assertEquals(cause, withBoth.getCause());
    }

    @Test
    @DisplayName("VersionConflictException constructors")
    void testVersionConflictExceptionConstructors() {
        VersionConflictException noArg = new VersionConflictException();
        assertNotNull(noArg);

        VersionConflictException withMessage = new VersionConflictException("conflict");
        assertEquals("conflict", withMessage.getMessage());

        Throwable cause = new RuntimeException("cause");
        VersionConflictException withCause = new VersionConflictException(cause);
        assertEquals(cause, withCause.getCause());

        VersionConflictException withBoth = new VersionConflictException("m", cause);
        assertEquals("m", withBoth.getMessage());
        assertEquals(cause, withBoth.getCause());
    }

}
