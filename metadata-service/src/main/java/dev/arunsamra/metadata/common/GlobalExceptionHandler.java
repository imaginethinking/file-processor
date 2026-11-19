package dev.arunsamra.metadata.common;

import dev.arunsamra.common.rest.dto.ApiErrorResponse;
import dev.arunsamra.common.rest.exception.BadRequestException;
import dev.arunsamra.common.rest.exception.RestException;
import dev.arunsamra.common.rest.exception.ValidationException;
import dev.arunsamra.metadata.xml.exception.InvalidXmlException;
import dev.arunsamra.metadata.xml.exception.MarshallingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidXmlException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidXml(InvalidXmlException ex) {
        log.warn("Rejected invalid XML: {}", ex.getMessage());
        return createErrorResponse(new BadRequestException("Request body is not valid metadata XML"));
    }

    @ExceptionHandler(MarshallingException.class)
    public ResponseEntity<ApiErrorResponse> handleMarshalling(MarshallingException ex) {
        log.error("XML processing failed", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to process metadata"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, List<String>> errors = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.computeIfAbsent(error.getField(), key -> new ArrayList<>())
                    .add(error.getDefaultMessage());
        }

        return createErrorResponse(new ValidationException(errors));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String message = ex.getMessage() != null && ex.getMessage().contains("Required request body is missing")
                ? "Request body is required"
                : "Request body contains invalid JSON";

        return createErrorResponse(new BadRequestException(message));
    }

    private ResponseEntity<ApiErrorResponse> createErrorResponse(RestException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(ApiErrorResponse.of(ex.getStatus(), ex.getMessage(), ex.getErrors()));
    }
}