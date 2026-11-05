package dev.arunsamra.fileprocessor.common;

import dev.arunsamra.fileprocessor.common.dto.ApiErrorResponse;
import dev.arunsamra.fileprocessor.common.exception.RestException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RestException.class)
    public ResponseEntity<ApiErrorResponse> handleRestException(RestException ex, HttpServletRequest req) {
        return createErrorResponse(ex, req);
    }


    private ResponseEntity<ApiErrorResponse> createErrorResponse(RestException ex, HttpServletRequest req) {
        ApiErrorResponse response = ApiErrorResponse.of(
                ex.getStatus(),
                ex.getMessage(),
                req.getRequestURI(),
                ex.getErrors()
        );

        return ResponseEntity
                .status(ex.getStatus())
                .body(response);
    }
}
