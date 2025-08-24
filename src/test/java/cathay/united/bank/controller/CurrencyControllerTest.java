package cathay.united.bank.controller;

import cathay.united.bank.model.mapper.ExchangeRateMapperImpl;
import cathay.united.bank.model.request.CurrencyCreateRequest;
import cathay.united.bank.model.request.CurrencyUpdateRequest;
import cathay.united.bank.repository.CurrencyRepository;
import cathay.united.bank.repository.ExchangeRateRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static cathay.united.bank.constant.ErrorMessages.CURRENCY_NOT_FOUND_BY_ID;
import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional // Rollback database changes after each test
@Import(ExchangeRateMapperImpl.class)
class CurrencyControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("Should perform complete Currency Controller")
    void shouldPerformCompleteCrudOperationsOnCurrency() throws Exception {
        // Step 1: Create a new currency
        CurrencyCreateRequest createRequest = new CurrencyCreateRequest();
        createRequest.setCurrencyCode("CAD");
        createRequest.setCurrencyName("Canadian Dollar");
        createRequest.setCountry("Canada");
        createRequest.setSymbol("C$");
        createCurrency(createRequest);
        // Step 2: Retrieve the currency by code
        getCurrencyByCode();
        // Step 3: Get all currencies (should include the new one)
        getListCurrencies();
        // Step 4: Update the currency
        CurrencyUpdateRequest updateRequest = new CurrencyUpdateRequest();
        updateRequest.setCurrencyCode("CAD");
        updateRequest.setCurrencyName("Canadian Dollar - Updated");
        updateRequest.setSymbol("CA$");
        updateCurrency(updateRequest);
        // Step 5: Delete currency
        deleteCurrentCy(4L);
    }

    private void createCurrency(CurrencyCreateRequest createRequest) throws Exception {

        mockMvc.perform(post("/currency")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    private void getCurrencyByCode() throws Exception {
        mockMvc.perform(get("/currency")
                        .param("code", "CAD"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currency_code").value("CAD"))
                .andExpect(jsonPath("$.currency_name").value("Canadian Dollar"))
                .andExpect(jsonPath("$.country").value("Canada"))
                .andExpect(jsonPath("$.symbol").value("C$"));
    }

    private void getListCurrencies() throws Exception {
        mockMvc.perform(get("/currency/list"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].currency_code").value("CAD"));
    }

    private void updateCurrency(CurrencyUpdateRequest currencyUpdateRequest) throws Exception {
        mockMvc.perform(put("/currency/{id}", 4L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(currencyUpdateRequest)))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get("/currency")
                        .param("code", "CAD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currency_name").value("Canadian Dollar - Updated"))
                .andExpect(jsonPath("$.symbol").value("CA$"));
    }

    private void deleteCurrentCy(Long id) throws Exception {
        mockMvc.perform(delete("/currency/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get("/currency/{id}", id))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString(CURRENCY_NOT_FOUND_BY_ID.formatted(id))));
    }
}