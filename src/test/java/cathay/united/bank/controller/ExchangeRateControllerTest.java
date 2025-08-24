package cathay.united.bank.controller;

import cathay.united.bank.model.mapper.ExchangeRateMapperImpl;
import cathay.united.bank.service.ExchangeRateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static cathay.united.bank.constant.ErrorMessages.START_DATE_AFTER_END_DATE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(ExchangeRateMapperImpl.class)
class ExchangeRateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ExchangeRateService exchangeRateService;

    @Test
    void testGetExchangeRates_FullFlow() throws Exception {
        mockMvc.perform(get("/exchange-rate")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].base_currency.currency_code").value("VND"))
                .andExpect(jsonPath("$.data[0].quote_currency").value("USD"))
                .andExpect(jsonPath("$.total").value(10))
                .andExpect(jsonPath("$.page").value(1));
    }

    @Test
    void testGetExchangeRates_FullFilter() throws Exception {
        mockMvc.perform(get("/exchange-rate?currencyCode=VND&page=1&size=20&sort=rateDate&sortDirection=ASC&startDate" +
                            "=2025-08-14T16:59:59Z&endDate=2025-08-15T16:59:59Z")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].base_currency.currency_code").value("VND"))
                .andExpect(jsonPath("$.data[0].quote_currency").value("USD"))
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.page").value(1));
    }

    @Test
    void testGetExchangeRates_DateException() throws Exception {
        mockMvc.perform(get("/exchange-rate?currencyCode=VND&page=1&size=20&sort=rateDate&sortDirection=ASC&startDate" +
                            "=2025-09-14T16:59:59Z&endDate=2025-08-15T16:59:59Z")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(START_DATE_AFTER_END_DATE));
    }

    @Test
    void testGetExchangeRatesByCurrency_Success() throws Exception {
        mockMvc.perform(get("/exchange-rate/currency/EUR")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.base_currency").value("EUR"))
                .andExpect(jsonPath("$.exchange_rates").isArray())
                .andExpect(jsonPath("$.total_records").exists())
                .andExpect(jsonPath("$.update_time").exists());
    }

    @Test
    void testGetExchangeRatesByCurrency_NotFound() throws Exception {
        mockMvc.perform(get("/exchange-rate/currency/INVALID")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}