package dev.arunsamra.common.rest.exception;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;

public class ValidationException extends RestException {
  public ValidationException(Map<String, List<String>> errors) {
    super(HttpStatus.BAD_REQUEST, "Validation failed", errors);
  }

  public ValidationException(String message, Map<String, List<String>> errors) {
    super(HttpStatus.BAD_REQUEST, message, errors);
  }
}
