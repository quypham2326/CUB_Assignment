package cathay.united.bank.service.feign;

import cathay.united.bank.config.FeignLogConfig;
import cathay.united.bank.model.request.ExchangeRateApiRequest;
import cathay.united.bank.model.response.ExchangeRateApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "ExchangeRateService", url = "${api.exchange-rates}", configuration = FeignLogConfig.class)
@Component
public interface ExchangeRateFeignService {
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    ExchangeRateApiResponse getExchangeRates(@SpringQueryMap ExchangeRateApiRequest request);
}
