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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static cathay.united.bank.constant.ErrorMessageKeys.CURRENCY_NOT_FOUND_BY_CODE;
import static cathay.united.bank.constant.ErrorMessageKeys.START_DATE_AFTER_END_DATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExchangeRateServiceImplTest {
    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @Mock
    private ExchangeRateMapper exchangeRateMapper;

    @Mock
    private ExchangeRateFeignService exchangeRateFeignService;

    @Mock
    private CurrencyRepository currencyRepository;

    @InjectMocks
    private ExchangeRateServiceImpl exchangeRateService;

    private MessageService messageService;

    private CurrencyEntity usdCurrency;
    private CurrencyEntity eurCurrency;
    private ExchangeRateApiResponse apiResponse;
    private ExchangeRateApiResponseItem apiResponseItem;
    private ExchangeRate exchangeRate;
    private ExchangeRateItemDTO exchangeRateItemDTO;

    @BeforeEach
    void setUp() {
        usdCurrency = new CurrencyEntity();
        usdCurrency.setCurrencyCode("USD");
        usdCurrency.setId(1L);

        eurCurrency = new CurrencyEntity();
        eurCurrency.setCurrencyCode("EUR");
        eurCurrency.setId(2L);

        apiResponseItem = new ExchangeRateApiResponseItem();
        apiResponseItem.setQuoteCurrency("EUR");
        apiResponseItem.setCloseTime(Instant.now());
        apiResponseItem.setAverageBid(new BigDecimal("1.0500"));
        apiResponseItem.setAverageAsk(new BigDecimal("1.0600"));
        apiResponseItem.setHighBid(new BigDecimal("1.0550"));
        apiResponseItem.setHighAsk(new BigDecimal("1.0650"));
        apiResponseItem.setLowBid(new BigDecimal("1.0450"));
        apiResponseItem.setLowAsk(new BigDecimal("1.0500"));

        apiResponse = new ExchangeRateApiResponse();
        apiResponse.setResponse(Arrays.asList(apiResponseItem));

        exchangeRate = new ExchangeRate();
        exchangeRate.setId(1L);
        exchangeRate.setBaseCurrency(usdCurrency);
        exchangeRate.setQuoteCurrency("EUR");

        exchangeRateItemDTO = new ExchangeRateItemDTO();
        exchangeRateItemDTO.setId(1L);

        var messageSource = new org.springframework.context.support.ResourceBundleMessageSource();
        messageSource.setBasename("messages_en");
        messageSource.setDefaultEncoding("UTF-8");
        this.messageService = new MessageService(messageSource);
    }

    @Test
    void scheduleExchangeRate_Success() {
        List<CurrencyEntity> currencies = Arrays.asList(usdCurrency, eurCurrency);
        when(currencyRepository.findAll()).thenReturn(currencies);
        when(exchangeRateFeignService.getExchangeRates(any(ExchangeRateApiRequest.class)))
                .thenReturn(apiResponse);

        exchangeRateService.scheduleExchangeRate();

        verify(currencyRepository).findAll();
        verify(exchangeRateFeignService, times(2)).getExchangeRates(any(ExchangeRateApiRequest.class));
        verify(exchangeRateRepository, times(2)).saveAll(any(List.class));
        verify(exchangeRateRepository).removeByDateRange(any(Instant.class), any(Instant.class));
    }

    @Test
    void scheduleExchangeRate_EmptyApiResponse() {
        List<CurrencyEntity> currencies = Arrays.asList(usdCurrency);
        ExchangeRateApiResponse emptyResponse = new ExchangeRateApiResponse();
        emptyResponse.setResponse(Collections.emptyList());

        when(currencyRepository.findAll()).thenReturn(currencies);
        when(exchangeRateFeignService.getExchangeRates(any(ExchangeRateApiRequest.class)))
                .thenReturn(emptyResponse);

        exchangeRateService.scheduleExchangeRate();

        verify(currencyRepository).findAll();
        verify(exchangeRateFeignService).getExchangeRates(any(ExchangeRateApiRequest.class));
        verify(exchangeRateRepository, never()).saveAll(any(List.class));
        verify(exchangeRateRepository).removeByDateRange(any(Instant.class), any(Instant.class));
    }

    @Test
    void getExchangeRates_Success() {
        ExchangeRateFilterRequest request = new ExchangeRateFilterRequest();
        request.setPage(1);
        request.setSize(10);
        request.setSort("id");
        request.setSortDirection(Sort.Direction.DESC);
        request.setStartDate(LocalDate.of(2025, 1, 1)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant());
        request.setEndDate(LocalDate.of(2025, 1, 31)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant());

        List<ExchangeRate> exchangeRates = Arrays.asList(exchangeRate);
        Page<ExchangeRate> exchangeRatePage = new PageImpl<>(exchangeRates,
                PageRequest.of(0, 10, Sort.Direction.DESC, "id"), 1);
        List<ExchangeRateItemDTO> exchangeRateItemDTOS = Arrays.asList(exchangeRateItemDTO);

        when(exchangeRateRepository.fetchExchangeRates(eq(request), any(Pageable.class)))
                .thenReturn(exchangeRatePage);
        when(exchangeRateMapper.mapperExchangeRatesToDTOList(exchangeRates))
                .thenReturn(exchangeRateItemDTOS);

        PaginationResponse<List<ExchangeRateItemDTO>> result = exchangeRateService.getExchangeRates(request);

        assertNotNull(result);
        assertEquals(exchangeRateItemDTOS, result.getData());
        assertEquals(1, result.getPage());
        assertEquals(10, result.getPageSize());
        assertEquals(1, result.getTotalPages());
        assertEquals(1, result.getTotal());
    }

    @Test
    void getExchangeRates_WithDefaultSortParameters() {
        ExchangeRateFilterRequest request = new ExchangeRateFilterRequest();
        request.setPage(1);
        request.setSize(10);

        List<ExchangeRate> exchangeRates = Arrays.asList(exchangeRate);
        Page<ExchangeRate> exchangeRatePage = new PageImpl<>(exchangeRates,
                PageRequest.of(0, 10, Sort.Direction.DESC, "id"), 1);
        List<ExchangeRateItemDTO> exchangeRateItemDTOS = Arrays.asList(exchangeRateItemDTO);

        when(exchangeRateRepository.fetchExchangeRates(eq(request), any(Pageable.class)))
                .thenReturn(exchangeRatePage);
        when(exchangeRateMapper.mapperExchangeRatesToDTOList(exchangeRates))
                .thenReturn(exchangeRateItemDTOS);

        PaginationResponse<List<ExchangeRateItemDTO>> result = exchangeRateService.getExchangeRates(request);

        assertNotNull(result);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(exchangeRateRepository).fetchExchangeRates(eq(request), pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();
        assertEquals(Sort.Direction.DESC, capturedPageable.getSort().getOrderFor("id").getDirection());
        assertEquals("id", capturedPageable.getSort().getOrderFor("id").getProperty());
    }

    @Test
    void getExchangeRates_EndDateBeforeStartDate_ThrowsException() {
        ExchangeRateFilterRequest request = new ExchangeRateFilterRequest();
        request.setPage(1);
        request.setSize(10);
        request.setStartDate(LocalDate.of(2025, 2, 1)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant());
        request.setEndDate(LocalDate.of(2025, 1, 31)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant());
        exchangeRateService = new ExchangeRateServiceImpl(
                exchangeRateRepository,
                exchangeRateMapper,
                exchangeRateFeignService,
                currencyRepository,
                messageService);
        CommonException exception = assertThrows(CommonException.class, () ->
                exchangeRateService.getExchangeRates(request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatusCode());
        assertEquals(messageService.getMessage(START_DATE_AFTER_END_DATE), exception.getMessage());
    }

    @Test
    void getExchangeRatesByCurrency_Success() {
        String baseCurrency = "USD";
        List<ExchangeRate> exchangeRates = Arrays.asList(exchangeRate);
        List<ExchangeRateItemDTO> exchangeRateItemDTOS = Arrays.asList(exchangeRateItemDTO);
        Instant latestUpdateTime = Instant.now();

        when(currencyRepository.findByCurrencyCode(baseCurrency))
                .thenReturn(Optional.of(usdCurrency));
        when(exchangeRateRepository.findTop10ByBaseCurrencyOrderByRateDateDesc(usdCurrency))
                .thenReturn(exchangeRates);
        when(exchangeRateRepository.findLatestUpdateTimeByBaseCurrency(usdCurrency))
                .thenReturn(Optional.of(latestUpdateTime));
        when(exchangeRateMapper.mapperExchangeRatesToDTOList(exchangeRates))
                .thenReturn(exchangeRateItemDTOS);

        ExchangeRateDTO result = exchangeRateService.getExchangeRatesByCurrency(baseCurrency);

        assertNotNull(result);
        assertEquals(baseCurrency, result.getBaseCurrency());
        assertEquals(exchangeRateItemDTOS, result.getExchangeRateItemDTOS());
        assertEquals(1, result.getTotalRecords());
        assertNotNull(result.getUpdateTime());

        verify(currencyRepository).findByCurrencyCode(baseCurrency);
        verify(exchangeRateRepository).findTop10ByBaseCurrencyOrderByRateDateDesc(usdCurrency);
        verify(exchangeRateRepository).findLatestUpdateTimeByBaseCurrency(usdCurrency);
        verify(exchangeRateMapper).mapperExchangeRatesToDTOList(exchangeRates);
    }

    @Test
    void getExchangeRatesByCurrency_CurrencyNotFound_ThrowsException() {
        String baseCurrency = "INVALID";

        when(currencyRepository.findByCurrencyCode(baseCurrency))
                .thenReturn(Optional.empty());
        exchangeRateService = new ExchangeRateServiceImpl(
                exchangeRateRepository,
                exchangeRateMapper,
                exchangeRateFeignService,
                currencyRepository,
                messageService);
        CommonException exception = assertThrows(CommonException.class, () ->
                exchangeRateService.getExchangeRatesByCurrency(baseCurrency));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatusCode());
        assertEquals(messageService.getMessage(CURRENCY_NOT_FOUND_BY_CODE, baseCurrency), exception.getMessage());

        verify(currencyRepository).findByCurrencyCode(baseCurrency);
        verify(exchangeRateRepository, never()).findTop10ByBaseCurrencyOrderByRateDateDesc(any());
    }

    @Test
    void getExchangeRatesByCurrency_EmptyData_UsesCurrentTime() {
        String baseCurrency = "USD";
        List<ExchangeRate> emptyExchangeRates = Collections.emptyList();
        List<ExchangeRateItemDTO> emptyExchangeRateItemDTOS = Collections.emptyList();

        when(currencyRepository.findByCurrencyCode(baseCurrency))
                .thenReturn(Optional.of(usdCurrency));
        when(exchangeRateRepository.findTop10ByBaseCurrencyOrderByRateDateDesc(usdCurrency))
                .thenReturn(emptyExchangeRates);
        when(exchangeRateRepository.findLatestUpdateTimeByBaseCurrency(usdCurrency))
                .thenReturn(Optional.empty()); // No data case
        when(exchangeRateMapper.mapperExchangeRatesToDTOList(emptyExchangeRates))
                .thenReturn(emptyExchangeRateItemDTOS);

        ExchangeRateDTO result = exchangeRateService.getExchangeRatesByCurrency(baseCurrency);

        assertNotNull(result);
        assertEquals(baseCurrency, result.getBaseCurrency());
        assertEquals(emptyExchangeRateItemDTOS, result.getExchangeRateItemDTOS());
        assertEquals(0, result.getTotalRecords());
        assertNotNull(result.getUpdateTime()); // Should fallback to current time

        verify(currencyRepository).findByCurrencyCode(baseCurrency);
        verify(exchangeRateRepository).findTop10ByBaseCurrencyOrderByRateDateDesc(usdCurrency);
        verify(exchangeRateRepository).findLatestUpdateTimeByBaseCurrency(usdCurrency);
        verify(exchangeRateMapper).mapperExchangeRatesToDTOList(emptyExchangeRates);
    }
}