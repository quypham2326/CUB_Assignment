package cathay.united.bank.exception;

import cathay.united.bank.model.dto.CommonResponse;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static cathay.united.bank.constant.ErrorMessageKeys.VALIDATION_ERROR;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private String getMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return returnError(HttpStatus.BAD_REQUEST,
                getMessage(VALIDATION_ERROR), parseError(ex.getBindingResult())
        );
    }

    @ExceptionHandler(value = CommonException.class)
    protected ResponseEntity<CommonResponse<Object>> handleCommonException(CommonException ex) {
        return returnError(
                ex.getHttpStatusCode(),
                ex.getMessage(),
                new ArrayList<>()
        );
    }

    @ExceptionHandler(value = {
            ValidationException.class,
            IllegalArgumentException.class,
            DataAccessException.class
    })
    public CommonResponse<Void> handleSpecificException(Exception ex) {
        log.error("Business exception: ", ex);
        return CommonResponse.<Void>builder()
                .code(400)
                .message(ex.getMessage())
                .build();
    }

    private Map<String, String> parseError(BindingResult bindingResult) {
        Map<String, String> errorMap = new HashMap<>();
        bindingResult.getFieldErrors().forEach(fieldError -> {
            String snakeCaseField = camelToSnakeCase(fieldError.getField());
            errorMap.put(snakeCaseField, String.format(getMessage(fieldError.getDefaultMessage()), snakeCaseField));
        });
        return errorMap;
    }

    private String camelToSnakeCase(String camelCase) {
        return camelCase.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }

    public static ResponseEntity<CommonResponse<Object>> returnError(HttpStatus status, String message, Object body) {
        return new ResponseEntity<>(CommonResponse.builder()
                .code(status.value())
                .data(body)
                .message(message)
                .build(), status);
    }
}
