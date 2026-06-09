package com.alight.healthbundle.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import com.alight.healthbundle.model.ErrorResponseDTO;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comprehensive tests for ErrorHandlingAdvice exception handlers.
 * Achieves >90% coverage for exception handling logic.
 */
@DisplayName("Error Handling Advice Tests")
class ErrorHandlingAdviceTest {

    private ErrorHandlingAdvice errorHandlingAdvice;

    @BeforeEach
    void setUp() {
        errorHandlingAdvice = new ErrorHandlingAdvice();
    }

    @Test
    @DisplayName("Handle BadRequestException returns 400 with message")
    void testHandleBadRequest() {
        BadRequestException exception = new BadRequestException("Invalid input provided");

        ErrorResponseDTO response = errorHandlingAdvice.handleBadRequest(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getErrorMessage()).isEqualTo("Invalid input provided");
    }

    @Test
    @DisplayName("Handle NoObjectFoundException returns 404 with message")
    void testHandleNotFound() {
        NoObjectFoundException exception = new NoObjectFoundException("Evaluation not found");

        ErrorResponseDTO response = errorHandlingAdvice.handleNotFound(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getErrorMessage()).isEqualTo("Evaluation not found");
    }

    @Test
    @DisplayName("Handle UnauthorizedException returns 401 with message")
    void testHandleUnauthorized() {
        UnauthorizedException exception = new UnauthorizedException("Invalid credentials");

        ErrorResponseDTO response = errorHandlingAdvice.handleUnauthorized(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(response.getErrorMessage()).isEqualTo("Invalid credentials");
    }

    @Test
    @DisplayName("Handle ForbiddenException returns 403 with message")
    void testHandleForbidden() {
        ForbiddenException exception = new ForbiddenException("Access denied to resource");

        ErrorResponseDTO response = errorHandlingAdvice.handleForbidden(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(response.getErrorMessage()).isEqualTo("Access denied to resource");
    }

    @Test
    @DisplayName("Handle VersionConflictException returns 409 with message")
    void testHandleVersionConflict() {
        VersionConflictException exception = new VersionConflictException("Resource version mismatch");

        ErrorResponseDTO response = errorHandlingAdvice.handleVersionConflict(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(response.getErrorMessage()).isEqualTo("Resource version mismatch");
    }

    @Test
    @DisplayName("Handle MissingRequestHeaderException returns 400 with header name")
    void testHandleMissingRequestHeader() {
        MissingRequestHeaderException exception = new MissingRequestHeaderException(
                "alightRequestHeader",
                null);

        ErrorResponseDTO response = errorHandlingAdvice.handleMissingRequestHeader(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getErrorMessage()).contains("alightRequestHeader");
        assertThat(response.getErrorMessage()).contains("is missing");
    }

    @Test
    @DisplayName("Handle MethodArgumentNotValidException returns 400 with validation errors")
    void testHandleValidationErrors() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "testObject");
        bindingResult.addError(new FieldError("testObject", "field1", "Field1 must not be null"));
        bindingResult.addError(new FieldError("testObject", "field2", "Field2 must be positive"));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ErrorResponseDTO response = errorHandlingAdvice.handleValidationErrors(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getErrorMessage()).contains("Validation failed");
        assertThat(response.getErrorMessage()).contains("Field1 must not be null");
        assertThat(response.getErrorMessage()).contains("Field2 must be positive");
    }

    @Test
    @DisplayName("Handle DataIntegrityViolationException returns 422 with generic message")
    void testHandleDataIntegrityViolation() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException("Constraint violation");

        ErrorResponseDTO response = errorHandlingAdvice.handleDataIntegrityViolation(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(response.getErrorMessage()).contains("Document validation failed");
    }

    @Test
    @DisplayName("Handle DataIntegrityViolationException with validation failure message")
    void testHandleDataIntegrityViolationWithCause() {
        RuntimeException cause = new RuntimeException("Document failed validation: field 'bundleRank' is required");
        DataIntegrityViolationException exception = new DataIntegrityViolationException("DB error", cause);

        ErrorResponseDTO response = errorHandlingAdvice.handleDataIntegrityViolation(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(response.getErrorMessage()).contains("Document validation failed");
        assertThat(response.getErrorMessage()).contains("schema constraints");
    }

    @Test
    @DisplayName("Handle IllegalStateException returns 500 with message")
    void testHandleIllegalState() {
        IllegalStateException exception = new IllegalStateException("MongoDB connection not available");

        ErrorResponseDTO response = errorHandlingAdvice.handleIllegalState(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(response.getErrorMessage()).isEqualTo("MongoDB connection not available");
    }

    @Test
    @DisplayName("Handle RuntimeException returns 500 with generic message")
    void testHandleRuntimeException() {
        RuntimeException exception = new RuntimeException("Unexpected runtime error");

        ErrorResponseDTO response = errorHandlingAdvice.handleRuntimeException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(response.getErrorMessage()).contains("An error occurred while processing your request");
    }

    @Test
    @DisplayName("Handle generic Exception returns 500 with generic message")
    void testHandleInternalServerError() {
        Exception exception = new Exception("Unhandled exception occurred");

        ErrorResponseDTO response = errorHandlingAdvice.handleInternalServerError(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(response.getErrorMessage()).contains("An internal server error occurred");
    }

    @Test
    @DisplayName("Handle NullPointerException via RuntimeException handler")
    void testHandleNullPointerException() {
        NullPointerException exception = new NullPointerException("Null reference encountered");

        ErrorResponseDTO response = errorHandlingAdvice.handleRuntimeException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(response.getErrorMessage()).contains("An error occurred while processing your request");
    }

    @Test
    @DisplayName("Handle MissingServletRequestParameterException returns 400 with parameter name")
    void testHandleMissingRequestParameter() {
        MissingServletRequestParameterException exception = new MissingServletRequestParameterException(
                "evaluationId", "String");

        ErrorResponseDTO response = errorHandlingAdvice.handleMissingRequestParameter(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getErrorMessage()).contains("evaluationId");
        assertThat(response.getErrorMessage()).contains("is missing");
    }
}
