package dev.arunsamra.metadata.xml.exception;

import dev.arunsamra.common.rest.exception.RestException;
import org.springframework.http.HttpStatus;

public class InvalidXmlException extends RestException {
    public InvalidXmlException(String message, Throwable cause) {
        super(HttpStatus.BAD_REQUEST, message, cause);
    }
}
