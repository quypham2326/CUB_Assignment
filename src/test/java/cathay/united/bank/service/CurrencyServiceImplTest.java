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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static cathay.united.bank.constant.ErrorMessageKeys.CANNOT_DELETE_CURRENCY_WITH_RATES;
import static cathay.united.bank.constant.ErrorMessageKeys.CURRENCY_ALREADY_EXISTS;
import static cathay.united.bank.constant.ErrorMessageKeys.CURRENCY_NOT_FOUND_BY_CODE;
import static cathay.united.bank.constant.ErrorMessageKeys.CURRENCY_NOT_FOUND_BY_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceImplTest {

    @InjectMocks
    private CurrencyServiceImpl currencyService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    private MessageService messageService;

    private CurrencyEntity currencyEntity;
    private CurrencyDTO currencyDTO;
    private CurrencyCreateRequest createRequest;
    private CurrencyUpdateRequest updateRequest;
    private CurrencyFilterRequest filterRequest;

    @BeforeEach
    void setUp() {
        currencyEntity = CurrencyEntity.builder()
                .id(1L)
                .currencyCode("USD")
                .currencyName("United States")
                .symbol("$")
                .build();

        currencyDTO = CurrencyDTO.builder()
                .id(1L)
                .currencyCode("USD")
                .currencyName("US Dollar")
                .country("United States")
                .symbol("$")
                .build();

        createRequest = CurrencyCreateRequest.builder()
                .currencyCode("EUR")
                .currencyName("Euro")
                .country("European Union")
                .symbol("€")
                .build();

        updateRequest = new CurrencyUpdateRequest();
        updateRequest.setCurrencyCode("USD");
        updateRequest.setCurrencyName("US Dollar Updated");
        updateRequest.setSymbol("$");

        filterRequest = new CurrencyFilterRequest();

        var messageSource = new org.springframework.context.support.ResourceBundleMessageSource();
        messageSource.setBasename("messages_en");
        messageSource.setDefaultEncoding("UTF-8");
        this.messageService = new MessageService(messageSource);

        currencyService = new CurrencyServiceImpl(
                currencyRepository,
                exchangeRateRepository,
                objectMapper, messageService);
    }

    @Nested
    @DisplayName("Test get currency by code")
    class GetCurrencyByCodeTests {

        @Test
        @DisplayName("Should return currency when currency code exists")
        void shouldReturnCurrency_WhenCurrencyCodeExists() {
            String currencyCode = "USD";
            when(currencyRepository.findByCurrencyCode(currencyCode))
                    .thenReturn(Optional.of(currencyEntity));
            when(objectMapper.convertValue(currencyEntity, CurrencyDTO.class))
                    .thenReturn(currencyDTO);

            CurrencyDTO result = currencyService.getCurrencyByCode(currencyCode);

            assertNotNull(result);
            assertEquals("USD", result.getCurrencyCode());
            assertEquals("US Dollar", result.getCurrencyName());

            verify(currencyRepository).findByCurrencyCode(currencyCode);
            verify(objectMapper).convertValue(currencyEntity, CurrencyDTO.class);
        }

        @Test
        @DisplayName("Should throw CommonException when currency code does not exist")
        void shouldThrowCommonException_WhenCurrencyCodeDoesNotExist() {
            String currencyCode = "INVALID";
            when(currencyRepository.findByCurrencyCode(currencyCode))
                    .thenReturn(Optional.empty());

            assertThrows(CommonException.class, () -> currencyService.getCurrencyByCode(currencyCode))
                    .equals(messageService.getMessage(CURRENCY_NOT_FOUND_BY_CODE, ":INVALID"));
            verify(currencyRepository).findByCurrencyCode(currencyCode);
            verifyNoInteractions(objectMapper);
        }
    }

    @Nested
    @DisplayName("Tests get currency by id")
    class GetCurrencyByIdTests {

        @Test
        @DisplayName("Should return currency when currency ID exists")
        void shouldReturnCurrency_WhenCurrencyIdExists() {
            Long id = 1L;
            when(currencyRepository.findById(id))
                    .thenReturn(Optional.of(currencyEntity));
            when(objectMapper.convertValue(currencyEntity, CurrencyDTO.class))
                    .thenReturn(currencyDTO);

            CurrencyDTO result = currencyService.getCurrencyById(id);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("USD", result.getCurrencyCode());

            verify(currencyRepository).findById(id);
            verify(objectMapper).convertValue(currencyEntity, CurrencyDTO.class);
        }

        @Test
        @DisplayName("Should throw CommonException when currency ID does not exist")
        void shouldThrowCommonException_WhenCurrencyIdDoesNotExist() {
            Long id = 999L;
            when(currencyRepository.findById(id))
                    .thenReturn(Optional.empty());
            assertThrows(CommonException.class, () -> currencyService.getCurrencyById(id))
                    .equals(messageService.getMessage(CURRENCY_NOT_FOUND_BY_ID, "999"));

            verify(currencyRepository).findById(id);
            verifyNoInteractions(objectMapper);
        }
    }

    @Nested
    @DisplayName("Tests get List by filter")
    class GetCurrenciesTests {

        @Test
        @DisplayName("Should return list of currencies")
        void shouldReturnListOfCurrencies() {
            CurrencyEntity secondEntity = new CurrencyEntity();
            secondEntity.setId(2L);
            secondEntity.setCurrencyCode("EUR");

            List<CurrencyEntity> entities = List.of(currencyEntity, secondEntity);

            CurrencyDTO secondDTO = CurrencyDTO.builder()
                    .id(2L).currencyCode("EUR").build();

            when(currencyRepository.fetchCurrencies(filterRequest))
                    .thenReturn(entities);
            when(objectMapper.convertValue(currencyEntity, CurrencyDTO.class))
                    .thenReturn(currencyDTO);
            when(objectMapper.convertValue(secondEntity, CurrencyDTO.class))
                    .thenReturn(secondDTO);

            List<CurrencyDTO> result = currencyService.getCurrencies(filterRequest);

            assertEquals(2, result.size());

            assertEquals("USD", result.get(0).getCurrencyCode());
            assertEquals("EUR", result.get(1).getCurrencyCode());

            verify(currencyRepository).fetchCurrencies(filterRequest);
            verify(objectMapper, times(2)).convertValue(any(CurrencyEntity.class), eq(CurrencyDTO.class));
        }

        @Test
        @DisplayName("Should return empty list when no currencies found")
        void shouldReturnEmptyList_WhenNoCurrenciesFound() {
            when(currencyRepository.fetchCurrencies(filterRequest))
                    .thenReturn(List.of());

            List<CurrencyDTO> result = currencyService.getCurrencies(filterRequest);

            assertTrue(result.isEmpty());

            verify(currencyRepository).fetchCurrencies(filterRequest);
            verifyNoInteractions(objectMapper);
        }
    }

    @Nested
    @DisplayName("Tests save currency")
    class SaveCurrencyTests {

        @Test
        @DisplayName("Should save currency when currency code does not exist")
        void shouldSaveCurrency_WhenCurrencyCodeDoesNotExist() {
            when(currencyRepository.findByCurrencyCode(createRequest.getCurrencyCode()))
                    .thenReturn(Optional.empty());

            CurrencyEntity newEntity = new CurrencyEntity();
            newEntity.setCurrencyCode("EUR");

            when(objectMapper.convertValue(createRequest, CurrencyEntity.class))
                    .thenReturn(newEntity);

            currencyService.saveCurrency(createRequest);

            verify(currencyRepository).findByCurrencyCode(createRequest.getCurrencyCode());
            verify(objectMapper).convertValue(createRequest, CurrencyEntity.class);
            verify(currencyRepository).save(newEntity);
        }

        @Test
        @DisplayName("Should throw CommonException when currency code already exists")
        void shouldThrowCommonException_WhenCurrencyCodeAlreadyExists() {
            when(currencyRepository.findByCurrencyCode(createRequest.getCurrencyCode()))
                    .thenReturn(Optional.of(currencyEntity));
            assertThrows(CommonException.class, () -> currencyService.saveCurrency(createRequest))
                    .equals(messageService.getMessage(CURRENCY_ALREADY_EXISTS, "USD"));

            verify(currencyRepository).findByCurrencyCode(createRequest.getCurrencyCode());
            verify(currencyRepository, never()).save(any());
            verifyNoInteractions(objectMapper);
        }
    }

    @Nested
    @DisplayName("Tests update currency")
    class UpdateCurrencyTests {

        @Test
        @DisplayName("Should update currency when valid data provided")
        void shouldUpdateCurrency_WhenValidDataProvided() {
            Long id = 1L;
            when(currencyRepository.findById(id))
                    .thenReturn(Optional.of(currencyEntity));
            when(currencyRepository.findByCurrencyCode(updateRequest.getCurrencyCode()))
                    .thenReturn(Optional.of(currencyEntity)); // Same entity

            currencyService.updateCurrency(id, updateRequest);

            assertEquals(updateRequest.getCurrencyCode(), currencyEntity.getCurrencyCode());
            assertEquals(updateRequest.getCurrencyName(), currencyEntity.getCurrencyName());
            assertEquals(updateRequest.getSymbol(), currencyEntity.getSymbol());

            verify(currencyRepository).findById(id);
            verify(currencyRepository).findByCurrencyCode(updateRequest.getCurrencyCode());
            verify(currencyRepository).save(currencyEntity);
        }

        @Test
        @DisplayName("Should throw CommonException when currency ID does not exist")
        void shouldThrowCommonException_WhenCurrencyIdDoesNotExist() {
            Long id = 999L;
            when(currencyRepository.findById(id))
                    .thenReturn(Optional.empty());
            assertThrows(CommonException.class, () -> currencyService.updateCurrency(id, updateRequest)).equals(
                    messageService.getMessage(CURRENCY_NOT_FOUND_BY_ID, "999"));

            verify(currencyRepository).findById(id);
            verify(currencyRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw CommonException when currency code exists for different ID")
        void shouldThrowCommonException_WhenCurrencyCodeExistsForDifferentId() {
            Long id = 1L;
            CurrencyEntity existingEntity = new CurrencyEntity();
            existingEntity.setId(2L); // Different ID
            existingEntity.setCurrencyCode("USD");

            when(currencyRepository.findById(id))
                    .thenReturn(Optional.of(currencyEntity));
            when(currencyRepository.findByCurrencyCode(updateRequest.getCurrencyCode()))
                    .thenReturn(Optional.of(existingEntity));

            assertThrows(CommonException.class, () -> currencyService.updateCurrency(id, updateRequest)).equals(
                    messageService.getMessage(CURRENCY_ALREADY_EXISTS, "USD"));

            verify(currencyRepository).findById(id);
            verify(currencyRepository).findByCurrencyCode(updateRequest.getCurrencyCode());
            verify(currencyRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should update currency when currency code does not exist")
        void shouldUpdateCurrency_WhenCurrencyCodeDoesNotExist() {
            Long id = 1L;
            when(currencyRepository.findById(id))
                    .thenReturn(Optional.of(currencyEntity));
            when(currencyRepository.findByCurrencyCode(updateRequest.getCurrencyCode()))
                    .thenReturn(Optional.empty());

            currencyService.updateCurrency(id, updateRequest);

            verify(currencyRepository).findById(id);
            verify(currencyRepository).findByCurrencyCode(updateRequest.getCurrencyCode());
            verify(currencyRepository).save(currencyEntity);
        }
    }

    @Nested
    @DisplayName("Tests Delete Currency")
    class DeleteCurrencyTests {

        @Test
        @DisplayName("Should delete currency status when currency exists")
        void shouldUpdateCurrencyStatus_WhenCurrencyExists() {
            Long id = 1L;
            when(currencyRepository.findById(id))
                    .thenReturn(Optional.of(currencyEntity));

            when(exchangeRateRepository.existsByBaseCurrency(currencyEntity))
                    .thenReturn(false);

            currencyService.deleteCurrency(id);

            verify(currencyRepository).findById(id);
            verify(exchangeRateRepository).existsByBaseCurrency(currencyEntity);
            verify(currencyRepository).delete(currencyEntity);
        }

        @Test
        @DisplayName("Should throw CommonException when currency ID does not exist")
        void shouldThrowCommonException_WhenCurrencyIdDoesNotExist() {
            Long id = 999L;
            when(currencyRepository.findById(id))
                    .thenReturn(Optional.empty());

            assertThrows(CommonException.class, () -> currencyService.deleteCurrency(id))
                    .equals(messageService.getMessage(CURRENCY_NOT_FOUND_BY_ID, "999"));

            verify(currencyRepository).findById(id);
            verify(currencyRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Should throw CommonException when currency has existing exchange rate")
        void shouldThrowCommonException_WhenCurrencyHasExistingExchangeRate() {
            Long id = 999L;
            when(currencyRepository.findById(id))
                    .thenReturn(Optional.of(currencyEntity));
            when(exchangeRateRepository.existsByBaseCurrency(currencyEntity)).thenReturn(true);
            assertThrows(CommonException.class, () -> currencyService.deleteCurrency(id))
                    .equals(messageService.getMessage(CANNOT_DELETE_CURRENCY_WITH_RATES,
                            currencyEntity.getCurrencyCode()));

            verify(currencyRepository).findById(id);
            verify(exchangeRateRepository).existsByBaseCurrency(currencyEntity);
            verify(currencyRepository, never()).delete(any());
        }
    }
}