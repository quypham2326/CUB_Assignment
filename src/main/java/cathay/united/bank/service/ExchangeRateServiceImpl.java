package cathay.united.bank.service;

import cathay.united.bank.exception.CommonException;
import cathay.united.bank.model.dto.ExchangeRateDTO;
import cathay.united.bank.model.dto.ExchangeRateItemDTO;
import cathay.united.bank.model.dto.PaginationResponse;
import cathay.united.bank.model.entity.CurrencyEntity;
import cathay.united.bank.model.entity.ExchangeRate;
import cathay.united.bank.model.mapper.ExchangeRateMapper;
import cathay.united.bank.model.request.ExchangeRateApiRequest;
import cathay.united.bank.model.request.ExchangeRateFilterRequest;
import cathay.united.bank.model.response.ExchangeRateApiResponse;
import cathay.united.bank.model.response.ExchangeRateApiResponseItem;
import cathay.united.bank.repository.CurrencyRepository;
import cathay.united.bank.repository.ExchangeRateRepository;
import cathay.united.bank.service.feign.ExchangeRateFeignService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;

import static cathay.united.bank.constant.ErrorMessageKeys.CURRENCY_NOT_FOUND_BY_CODE;
import static cathay.united.bank.constant.ErrorMessageKeys.START_DATE_AFTER_END_DATE;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRateServiceImpl implements ExchangeRateService {
    private final ExchangeRateRepository exchangeRateRepository;
    private final ExchangeRateMapper exchangeRateMapper;
    private final ExchangeRateFeignService exchangeRateFeignService;
    private final CurrencyRepository currencyRepository;
    private final MessageService messageService;

    @Override
    @Scheduled(cron = "${cron.time}", zone = "${cron.area}")
    @Transactional
    public void scheduleExchangeRate() {
        List<CurrencyEntity> findAllCurrencies = currencyRepository.findAll();
        ExchangeRateApiRequest exchangeRateApiRequest =
                ExchangeRateApiRequest.builder().build();
        removeRecordInRange(exchangeRateApiRequest);
        findAllCurrencies.stream().forEach(currency -> {
            exchangeRateApiRequest.setBase(currency.getCurrencyCode());
            ExchangeRateApiResponse exchangeRateApiResponse =
                    exchangeRateFeignService.getExchangeRates(exchangeRateApiRequest);
            if (exchangeRateApiResponse.getResponse().isEmpty()) {
                return;
            }
            saveExchangeRatesEntity(exchangeRateApiResponse.getResponse(), currency);
        });

    }

    private void removeRecordInRange(ExchangeRateApiRequest request) {
        Instant start = LocalDate.parse(request.getStart_date())
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant();

        Instant end = LocalDate.parse(request.getEnd_date())
                .plusDays(1)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant();
        exchangeRateRepository.removeByDateRange(start, end);

    }

    public void saveExchangeRatesEntity(List<ExchangeRateApiResponseItem> response, CurrencyEntity currency) {
        List<ExchangeRate> entities = response.stream()
                .map(item -> {
                    ExchangeRate er = new ExchangeRate();
                    er.setBaseCurrency(currency);
                    er.setQuoteCurrency(item.getQuoteCurrency());
                    er.setRateDate(item.getCloseTime());
                    er.setAverageBid(item.getAverageBid());
                    er.setAverageAsk(item.getAverageAsk());
                    er.setHighBid(item.getHighBid());
                    er.setMidRate(calculateMidRate(item.getAverageBid(), item.getAverageAsk()));
                    er.setHighAsk(item.getHighAsk());
                    er.setLowBid(item.getLowBid());
                    er.setLowAsk(item.getLowAsk());
                    return er;
                })
                .toList();

        exchangeRateRepository.saveAll(entities);
        log.info("Exchange rates updated + " + currency.getCurrencyCode());
    }

    private BigDecimal calculateMidRate(BigDecimal averageBid, BigDecimal averageAsk) {
        return averageAsk.add(averageBid).divide(new BigDecimal("2"));
    }

    @Override
    public ExchangeRateDTO getExchangeRatesByCurrency(String baseCurrency) {
        CurrencyEntity currency = currencyRepository.findByCurrencyCode(baseCurrency)
                .orElseThrow(() -> new CommonException(HttpStatus.BAD_REQUEST,
                        messageService.getMessage(CURRENCY_NOT_FOUND_BY_CODE, baseCurrency)));

        List<ExchangeRate> exchangeRates = exchangeRateRepository.findTop10ByBaseCurrencyOrderByRateDateDesc(currency);

        List<ExchangeRateItemDTO> exchangeRateItemDTOS = exchangeRateMapper.mapperExchangeRatesToDTOList(exchangeRates);

        Instant latestUpdateInstant = exchangeRateRepository.findLatestUpdateTimeByBaseCurrency(currency)
                .orElse(Instant.now());

        LocalDateTime systemUpdateTime = latestUpdateInstant.atZone(ZoneOffset.UTC).toLocalDateTime();

        return ExchangeRateDTO.builder()
                .updateTime(systemUpdateTime)
                .exchangeRateItemDTOS(exchangeRateItemDTOS)
                .baseCurrency(baseCurrency)
                .totalRecords(exchangeRateItemDTOS.size())
                .build();
    }

    @Override
    public PaginationResponse<List<ExchangeRateItemDTO>> getExchangeRates(ExchangeRateFilterRequest request) {
        Pageable pageRequest = resolvePageable(
                request.getPage(),
                request.getSize(),
                request.getSort(),
                request.getSortDirection(),
                "id",
                Sort.Direction.DESC
        );
        if (request.getStartDate() != null && request.getEndDate() != null
            && request.getEndDate().isBefore(request.getStartDate())) {
            throw new CommonException(HttpStatus.BAD_REQUEST,
                    messageService.getMessage(START_DATE_AFTER_END_DATE));
        }
        request.setStartDate(request.getStartDate());
        request.setEndDate(request.getEndDate());
        Page<ExchangeRate> exchangeRates = exchangeRateRepository.fetchExchangeRates(request, pageRequest);

        return PaginationResponse.<List<ExchangeRateItemDTO>>builder()
                .data(exchangeRateMapper.mapperExchangeRatesToDTOList(exchangeRates.getContent()))
                .page(request.getPage())
                .pageSize(pageRequest.getPageSize())
                .totalPages(exchangeRates.getTotalPages())
                .total(exchangeRates.getNumberOfElements())
                .build();
    }

    private Pageable resolvePageable(
            Integer page,
            Integer pageSize,
            String sort,
            Sort.Direction sortDirection,
            String defaultSortProperty,
            Sort.Direction defaultSortDirection
    ) {
        sortDirection = Objects.isNull(sortDirection) ? defaultSortDirection : sortDirection;
        sort = StringUtils.isBlank(sort) ? defaultSortProperty : sort;
        return PageRequest.of(page - 1, pageSize, sortDirection, sort);
    }

}
