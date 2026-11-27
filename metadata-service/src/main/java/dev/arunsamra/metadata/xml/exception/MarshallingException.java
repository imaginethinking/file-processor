package dev.arunsamra.metadata.xml.exception;

import dev.arunsamra.common.rest.exception.RestException;
import org.springframework.http.HttpStatus;

public class MarshallingException extends RestException {
  public MarshallingException(String message, Throwable cause) {
    super(HttpStatus.INTERNAL_SERVER_ERROR, message, cause);
  }
}
