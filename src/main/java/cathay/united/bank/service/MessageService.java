package cathay.united.bank.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageSource messageSource;

    public String getMessage(String key) {
        return getMessage(key, (Object[]) null);
    }

    public String getMessage(String key, Object... params) {
        Locale locale = LocaleContextHolder.getLocale();
        return getMessageWithLocale(key, locale, params);
    }

    private String getMessageWithLocale(String key, Locale locale, Object... params) {
        try {
            String message = messageSource.getMessage(key, params, locale);
            return message;

        } catch (Exception e) {
            return key + (params != null && params.length > 0 ? " [" + Arrays.toString(params) + "]" : "");
        }
    }
}
