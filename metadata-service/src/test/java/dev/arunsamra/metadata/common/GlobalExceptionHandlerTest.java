package dev.arunsamra.metadata.common;

import dev.arunsamra.common.rest.dto.ApiErrorResponse;
import dev.arunsamra.metadata.xml.exception.InvalidXmlException;
import dev.arunsamra.metadata.xml.exception.MarshallingException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldReturnBadRequestForInvalidXml() {
        ResponseEntity<ApiErrorResponse> result = handler.handleInvalidXml(new InvalidXmlException("Error", null));

        assertEquals(400, result.getStatusCode().value());
    }

    @Test
    void shouldReturnServerErrorForMarshallingFailure() {
        ResponseEntity<ApiErrorResponse> result = handler.handleMarshalling(new MarshallingException("Error", null));

        assertEquals(500, result.getStatusCode().value());
    }
}
