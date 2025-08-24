package cathay.united.bank.service;

import cathay.united.bank.exception.CommonException;
import cathay.united.bank.model.dto.CurrencyDTO;
import cathay.united.bank.model.entity.CurrencyEntity;
import cathay.united.bank.model.request.CurrencyCreateRequest;
import cathay.united.bank.model.request.CurrencyFilterRequest;
import cathay.united.bank.model.request.CurrencyUpdateRequest;
import cathay.united.bank.repository.CurrencyRepository;
import cathay.united.bank.repository.ExchangeRateRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

import static cathay.united.bank.constant.ErrorMessageKeys.CANNOT_DELETE_CURRENCY_WITH_RATES;
import static cathay.united.bank.constant.ErrorMessageKeys.CURRENCY_ALREADY_EXISTS;
import static cathay.united.bank.constant.ErrorMessageKeys.CURRENCY_CODE_ALREADY_EXISTS;
import static cathay.united.bank.constant.ErrorMessageKeys.CURRENCY_DELETION_NOT_ALLOWED;
import static cathay.united.bank.constant.ErrorMessageKeys.CURRENCY_NOT_FOUND_BY_CODE;
import static cathay.united.bank.constant.ErrorMessageKeys.CURRENCY_NOT_FOUND_BY_ID;

@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final ObjectMapper objectMapper;
    private final MessageService messageService;

    @Override
    public CurrencyDTO getCurrencyByCode(String code) {
        CurrencyEntity currency = currencyRepository.findByCurrencyCode(code)
                .orElseThrow(() -> new CommonException(HttpStatus.BAD_REQUEST,
                        messageService.getMessage(CURRENCY_NOT_FOUND_BY_CODE, code)));

        return objectMapper.convertValue(currency, CurrencyDTO.class);
    }

    @Override
    public CurrencyDTO getCurrencyById(Long id) {
        CurrencyEntity currency = currencyRepository.findById(id)
                .orElseThrow(() -> new CommonException(HttpStatus.BAD_REQUEST,
                        messageService.getMessage(CURRENCY_NOT_FOUND_BY_ID, id)));

        return objectMapper.convertValue(currency, CurrencyDTO.class);
    }

    @Override
    public List<CurrencyDTO> getCurrencies(CurrencyFilterRequest request) {
        return currencyRepository.fetchCurrencies(request)
                .stream()
                .map(entity -> objectMapper.convertValue(entity, CurrencyDTO.class))
                .toList();
    }

    @Override
    public void saveCurrency(CurrencyCreateRequest createRequest) {
        currencyRepository.findByCurrencyCode(createRequest.getCurrencyCode())
                .ifPresent(c -> {
                    throw new CommonException(HttpStatus.BAD_REQUEST,
                            messageService.getMessage(CURRENCY_ALREADY_EXISTS, c.getCurrencyCode()));
                });

        currencyRepository.save(objectMapper.convertValue(createRequest, CurrencyEntity.class));
    }

    @Override
    public void updateCurrency(Long id, CurrencyUpdateRequest updateRequest) {
        CurrencyEntity currency = currencyRepository.findById(id)
                .orElseThrow(() -> new CommonException(
                        HttpStatus.BAD_REQUEST,
                        messageService.getMessage(CURRENCY_NOT_FOUND_BY_ID, id))
                );
        currencyRepository.findByCurrencyCode(updateRequest.getCurrencyCode())
                .ifPresent(c -> {
                    if (!c.getId().equals(id)) {
                        throw new CommonException(HttpStatus.BAD_REQUEST,
                                messageService.getMessage(CURRENCY_CODE_ALREADY_EXISTS, c.getCurrencyCode()));
                    }
                });

        currency.setCurrencyCode(updateRequest.getCurrencyCode());
        currency.setCurrencyName(updateRequest.getCurrencyName());
        currency.setSymbol(updateRequest.getSymbol());

        currencyRepository.save(currency);
    }

    @Override
    public void deleteCurrency(Long id) {
        CurrencyEntity currency = currencyRepository.findById(id)
                .orElseThrow(() -> new CommonException(
                        HttpStatus.BAD_REQUEST,
                        messageService.getMessage(CURRENCY_DELETION_NOT_ALLOWED)));
        if (exchangeRateRepository.existsByBaseCurrency(currency)) {
            throw new CommonException(HttpStatus.BAD_REQUEST,
                    messageService.getMessage(CANNOT_DELETE_CURRENCY_WITH_RATES, currency.getCurrencyCode()));
        }
        currencyRepository.delete(currency);
    }

}
