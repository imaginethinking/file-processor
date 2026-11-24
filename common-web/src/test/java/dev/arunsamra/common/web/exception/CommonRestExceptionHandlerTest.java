package dev.arunsamra.common.web.exception;

import dev.arunsamra.common.rest.dto.ApiErrorResponse;
import dev.arunsamra.common.rest.exception.BadRequestException;
import dev.arunsamra.common.rest.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CommonRestExceptionHandlerTest {

    private final CommonRestExceptionHandler handler = new CommonRestExceptionHandler();

    @Test
    void shouldHandleRestException() {
        BadRequestException exception = new BadRequestException("Invalid request");

        ResponseEntity<ApiErrorResponse> result = handler.handleRestException(exception);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(result.getBody())
                .isEqualTo(ApiErrorResponse.of(
                        HttpStatus.BAD_REQUEST,
                        "Invalid request",
                        null
                ));
    }

    @Test
    void shouldHandleRestExceptionWithErrors() {
        Map<String, List<String>> errors = Map.of(
                "fileName", List.of("must not be blank")
        );

        ValidationException exception = new ValidationException(errors);

        ResponseEntity<ApiErrorResponse> result = handler.handleRestException(exception);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().details()).isEqualTo(errors);
    }
}