package dev.arunsamra.fileprocessor.common;

import dev.arunsamra.fileprocessor.common.dto.ApiErrorResponse;
import dev.arunsamra.fileprocessor.common.exception.BadRequestException;
import dev.arunsamra.fileprocessor.common.exception.RestException;
import dev.arunsamra.fileprocessor.common.exception.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import tools.jackson.databind.exc.InvalidFormatException;

import java.util.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RestException.class)
    public ResponseEntity<ApiErrorResponse> handleRestException(
            RestException ex,
            HttpServletRequest req) {
        return createErrorResponse(ex, req);
    }


    private ResponseEntity<ApiErrorResponse> createErrorResponse(
            RestException ex,
            HttpServletRequest req) {
        ApiErrorResponse response = ApiErrorResponse.of(
                ex.getStatus(),
                ex.getMessage(),
                ex.getErrors()
        );

        return ResponseEntity
                .status(ex.getStatus())
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        Map<String, List<String>> errors = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.computeIfAbsent(error.getField(), key -> new ArrayList<>())
                    .add(error.getDefaultMessage());
        }

        ValidationException validationException = new ValidationException(errors);
        return createErrorResponse(validationException, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        String message;

        if (ex.getMessage() != null && ex.getMessage().contains("Required request body is missing")) {
            message = "Request body is required";

        } else if (ex.getCause() instanceof InvalidFormatException invalidFormatException) {
            message = handleInvalidFormat(invalidFormatException);

        } else {
            message = "Request body contains invalid JSON";
        }

        return createErrorResponse(
                new BadRequestException(message),
                request
        );
    }

    private String handleInvalidFormat(InvalidFormatException ex) {
        String field = ex.getPath().stream()
                .map(reference -> reference.getPropertyName())
                .filter(Objects::nonNull)
                .findFirst()
                .orElse("request body");

        return "Invalid value for field: " + field;
    }
}
