package dev.arunsamra.common.web.exception;

import dev.arunsamra.common.rest.dto.ApiErrorResponse;
import dev.arunsamra.common.rest.exception.BadRequestException;
import dev.arunsamra.common.rest.exception.RestException;
import dev.arunsamra.common.rest.exception.ValidationException;
import java.util.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tools.jackson.databind.exc.InvalidFormatException;

@RestControllerAdvice
public class CommonRestExceptionHandler {

  @ExceptionHandler(RestException.class)
  public ResponseEntity<ApiErrorResponse> handleRestException(RestException ex) {
    return createErrorResponse(ex);
  }

  private ResponseEntity<ApiErrorResponse> createErrorResponse(RestException ex) {
    ApiErrorResponse response =
        ApiErrorResponse.of(ex.getStatus(), ex.getMessage(), ex.getErrors());

    return ResponseEntity.status(ex.getStatus()).body(response);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex) {
    Map<String, List<String>> errors = new HashMap<>();

    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      String message =
          error.isBindingFailure()
              ? "Invalid value: " + error.getRejectedValue()
              : error.getDefaultMessage();

      errors.computeIfAbsent(error.getField(), key -> new ArrayList<>()).add(message);
    }

    return createErrorResponse(new ValidationException(errors));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex) {
    String message;

    if (ex.getMessage() != null && ex.getMessage().contains("Required request body is missing")) {
      message = "Request body is required";
    } else if (ex.getCause() instanceof InvalidFormatException invalidFormatException) {
      message = handleInvalidFormat(invalidFormatException);
    } else {
      message = "Request body contains invalid JSON";
    }

    return createErrorResponse(new BadRequestException(message));
  }

  private String handleInvalidFormat(InvalidFormatException ex) {
    String field =
        ex.getPath().stream()
            .map(reference -> reference.getPropertyName())
            .filter(Objects::nonNull)
            .findFirst()
            .orElse("request body");

    return "Invalid value for field: " + field;
  }
}
