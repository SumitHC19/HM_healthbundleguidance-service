package com.alight.healthbundle.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.alight.healthbundle.model.ErrorResponseDTO;
import com.alight.healthbundle.model.enums.FeaturedAs;

import java.util.Arrays;
import java.util.stream.Collectors;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ErrorHandlingAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ErrorHandlingAdvice.class);

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public ErrorResponseDTO handleBadRequest(BadRequestException exception) {
        logger.warn("Bad request: {}", exception.getMessage());
        return new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoObjectFoundException.class)
    public ErrorResponseDTO handleNotFound(NoObjectFoundException exception) {
        logger.warn("Resource not found: {}", exception.getMessage());
        return new ErrorResponseDTO(HttpStatus.NOT_FOUND.value(), exception.getMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public ErrorResponseDTO handleUnauthorized(UnauthorizedException exception) {
        logger.warn("Unauthorized access attempt: {}", exception.getMessage());
        return new ErrorResponseDTO(HttpStatus.UNAUTHORIZED.value(), exception.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ForbiddenException.class)
    public ErrorResponseDTO handleForbidden(ForbiddenException exception) {
        logger.warn("Forbidden access attempt: {}", exception.getMessage());
        return new ErrorResponseDTO(HttpStatus.FORBIDDEN.value(), exception.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(VersionConflictException.class)
    public ErrorResponseDTO handleVersionConflict(VersionConflictException exception) {
        logger.warn("Version conflict: {}", exception.getMessage());
        return new ErrorResponseDTO(HttpStatus.CONFLICT.value(), exception.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ErrorResponseDTO handleMissingRequestHeader(MissingRequestHeaderException exception) {
        String message = String.format("Required request header '%s' is missing", exception.getHeaderName());
        logger.warn("Missing request header: {}", exception.getHeaderName());
        return new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), message);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ErrorResponseDTO handleMissingRequestParameter(MissingServletRequestParameterException exception) {
        String message = String.format("Required request parameter '%s' is missing", exception.getParameterName());
        logger.warn("Missing request parameter: {}", exception.getParameterName());
        return new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), message);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponseDTO handleValidationErrors(MethodArgumentNotValidException exception) {
        String errors = exception.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        logger.warn("Validation failed: {}", errors);
        return new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), "Validation failed: " + errors);
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorResponseDTO handleDataIntegrityViolation(DataIntegrityViolationException exception) {
        String message = "Document validation failed. Please check your request data.";

        // Extract more specific error message if available
        if (exception.getCause() != null && exception.getCause().getMessage() != null) {
            String causeMessage = exception.getCause().getMessage();
            if (causeMessage.contains("Document failed validation")) {
                message = "Document validation failed. The data does not meet the required schema constraints.";
            }
        }

        logger.warn("Data integrity violation: {}", exception.getMessage());
        return new ErrorResponseDTO(HttpStatus.UNPROCESSABLE_ENTITY.value(), message);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IllegalStateException.class)
    public ErrorResponseDTO handleIllegalState(IllegalStateException exception) {
        logger.error("Illegal state exception: {}", exception.getMessage(), exception);
        return new ErrorResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ErrorResponseDTO handleRuntimeException(RuntimeException exception) {
        logger.error("Runtime exception [{}]: {}", exception.getClass().getSimpleName(), exception.getMessage(),
                exception);
        return new ErrorResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An error occurred while processing your request. Please try again later.");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponseDTO handleInternalServerError(Exception exception) {
        logger.error("Unhandled exception [{}]: {}", exception.getClass().getName(), exception.getMessage(), exception);
        return new ErrorResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An internal server error occurred. Please try again later.");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorResponseDTO handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        String message = String.format("invalid featuredAS value. Allowed values are: %s",
                Arrays.stream(FeaturedAs.values()).map(Enum::name).collect(Collectors.joining(", ")));
        logger.warn("Invalid faield: {}", exception.getMessage());
        return new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), message);
    }

}
