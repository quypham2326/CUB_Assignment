package cathay.united.bank.config;

import feign.Logger;
import feign.Request;
import feign.Response;
import feign.Util;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class CustomFeignLogger extends Logger {
    @Override
    protected void log(String s, String s1, Object... objects) {
        log.debug(String.format(methodTag(s) + s1, objects));
    }

    @Override
    protected void logRequest(String configKey, Level logLevel, Request request) {
        String body = request.body() != null ? new String(request.body(), StandardCharsets.UTF_8) : "{}";

        log.info("➡️ Feign Request: method={}, url={}, body={}",
                request.httpMethod(), request.url(), request.headers(), body);
    }

    @Override
    protected Response logAndRebufferResponse(String configKey, Level logLevel, Response response, long elapsedTime) throws IOException {
        byte[] bodyData = Util.toByteArray(response.body().asInputStream());
        String body = new String(bodyData, StandardCharsets.UTF_8);

        log.info("⬅️ Feign Response: status={}, duration={}ms, body={}",
                response.status(), elapsedTime, response.headers(), body);

        return response.toBuilder().body(bodyData).build();
    }
}
