package cathay.united.bank.service;

import cathay.united.bank.model.dto.ExchangeRateDTO;
import cathay.united.bank.model.dto.ExchangeRateItemDTO;
import cathay.united.bank.model.dto.PaginationResponse;
import cathay.united.bank.model.request.ExchangeRateFilterRequest;

import java.util.List;

public interface ExchangeRateService {

    void scheduleExchangeRate();

    PaginationResponse<List<ExchangeRateItemDTO>> getExchangeRates(ExchangeRateFilterRequest request);

    /**
     * New API that provides update time and currency-related information
     */
    ExchangeRateDTO getExchangeRatesByCurrency(String baseCurrency);

}
