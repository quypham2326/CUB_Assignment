package cathay.united.bank.exception;

import cathay.united.bank.model.dto.CommonResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {
    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private FieldError fieldError1;

    @Mock
    private MessageSource messageSource;

    @Nested
    class MethodArgumentNotValidExceptionTests {
        @BeforeEach
        void setUp() {
            when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        }

        @Test
        @DisplayName("Should handle single field validation error")
        void shouldHandleSingleFieldValidationError() {
            when(fieldError1.getField()).thenReturn("currencyCode");
            when(fieldError1.getDefaultMessage()).thenReturn("Currency code is required");
            when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1));
            when(messageSource.getMessage(any(), any(), any())).thenReturn("Validation Error");

            ResponseEntity<CommonResponse<Object>> response =
                    globalExceptionHandler.handleMethodArgumentNotValidException(methodArgumentNotValidException);

            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

            CommonResponse<Object> body = response.getBody();
            assertNotNull(body);
            assertEquals(400, body.getCode());
            assertEquals("Validation Error", body.getMessage());

        }
    }

    @Nested
    class CommonExceptionTests {

        @Test
        @DisplayName("Should handle CommonException with BAD_REQUEST status")
        void shouldHandleCommonExceptionWithBadRequestStatus() {
            // Given
            String errorMessage = "Currency not found";
            CommonException commonException = new CommonException(HttpStatus.BAD_REQUEST, errorMessage);

            // When
            ResponseEntity<CommonResponse<Object>> response =
                    globalExceptionHandler.handleCommonException(commonException);

            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

            CommonResponse<Object> body = response.getBody();
            assertNotNull(body);
            assertEquals(400, body.getCode());
            assertEquals(errorMessage, body.getMessage());
        }
    }

}