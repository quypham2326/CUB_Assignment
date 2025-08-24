package cathay.united.bank.service;

import cathay.united.bank.model.dto.CurrencyDTO;
import cathay.united.bank.model.request.CurrencyCreateRequest;
import cathay.united.bank.model.request.CurrencyFilterRequest;
import cathay.united.bank.model.request.CurrencyUpdateRequest;

import java.util.List;

public interface CurrencyService {

    CurrencyDTO getCurrencyByCode(String code);

    CurrencyDTO getCurrencyById(Long id);

    List<CurrencyDTO> getCurrencies(CurrencyFilterRequest request);

    void saveCurrency(CurrencyCreateRequest createRequest);

    void updateCurrency(Long id, CurrencyUpdateRequest createRequest);

    void deleteCurrency(Long id);

}
