package cathay.united.bank.controller;

import cathay.united.bank.model.dto.ExchangeRateDTO;
import cathay.united.bank.model.dto.ExchangeRateItemDTO;
import cathay.united.bank.model.dto.PaginationResponse;
import cathay.united.bank.model.request.ExchangeRateFilterRequest;
import cathay.united.bank.service.ExchangeRateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/exchange-rate")
@RequiredArgsConstructor
@Tag(name = "Exchange Rates", description = "APIs for retrieving and managing exchange rates")
public class ExchangeRateController {
    private final ExchangeRateService exchangeRateService;

    @GetMapping
    @Operation(
            summary = "Get exchange rates (paginated)",
            description = "Retrieve a list of exchange rates with pagination and filter support"
    )
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    public PaginationResponse<List<ExchangeRateItemDTO>> getExchangeRates(
            @Parameter(description = "Filter request with pagination and criteria") ExchangeRateFilterRequest request) {
        return exchangeRateService.getExchangeRates(request);
    }

    @GetMapping("/currency/{baseCurrency}")
    @Operation(
            summary = "Get exchange rates by base currency",
            description = "Retrieve exchange rates for a specific base currency"
    )
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "400", description = "Base currency not found")
    public ExchangeRateDTO getExchangeRatesByCurrency(
            @Parameter(description = "Base currency code", example = "USD")
            @PathVariable String baseCurrency) {
        return exchangeRateService.getExchangeRatesByCurrency(baseCurrency);
    }
}
