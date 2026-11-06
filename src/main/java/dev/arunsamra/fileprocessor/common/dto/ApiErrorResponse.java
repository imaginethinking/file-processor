package dev.arunsamra.fileprocessor.common.dto;

import dev.arunsamra.fileprocessor.processing.ProcessingStatus;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

public record ApiErrorResponse(
        ProcessingStatus status,
        String error,
        String message,
        String path,
        Map<String, List<String>> details
) {
    public static ApiErrorResponse of(HttpStatus httpStatus, String message, String path) {
        return new ApiErrorResponse(ProcessingStatus.FAILED, httpStatus.getReasonPhrase(), message, path, null);
    }

    public static ApiErrorResponse of(HttpStatus httpStatus, String message, String path, Map<String, List<String>> details) {
        return new ApiErrorResponse(ProcessingStatus.FAILED, httpStatus.getReasonPhrase(), message, path, details);
    }
}
