package dev.arunsamra.fileprocessor.common.exception;

import dev.arunsamra.common.rest.exception.RestException;
import org.springframework.http.HttpStatus;

public class MetadataServiceException extends RestException {

  public MetadataServiceException(String message, Throwable cause) {
    super(HttpStatus.BAD_GATEWAY, message, cause);
  }
}
