package cathay.united.bank.controller;

import cathay.united.bank.model.dto.CommonResponse;
import cathay.united.bank.model.dto.CurrencyDTO;
import cathay.united.bank.model.request.CurrencyCreateRequest;
import cathay.united.bank.model.request.CurrencyFilterRequest;
import cathay.united.bank.model.request.CurrencyUpdateRequest;
import cathay.united.bank.service.CurrencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/currency")
@Tag(name = "Currency", description = "APIs for managing currencies")
public class CurrencyController {
    private final CurrencyService currencyService;

    @GetMapping("/{id}")
    @Operation(
            summary = "Get currency by ID",
            description = "Retrieve detailed information of a currency by its database ID",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "400", description = "Currency not found")
    public CurrencyDTO getCurrency(@PathVariable Long id) {

        return currencyService.getCurrencyById(id);
    }

    @GetMapping("")
    @Operation(
            summary = "Get currency by code",
            description = "Retrieve currency information using its 3-letter code"
    )
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "400", description = "Invalid currency code format")
    public CurrencyDTO getCurrencyByCode(
            @RequestParam
            @Valid String code) {

        return currencyService.getCurrencyByCode(code);
    }

    @GetMapping("/list")
    @Operation(
            summary = "Get list of currencies",
            description = "Retrieve a list of currencies based on filter criteria"
    )
    @ApiResponse(responseCode = "200", description = "Successful operation")
    public List<CurrencyDTO> getListCurrencies(CurrencyFilterRequest request) {

        return currencyService.getCurrencies(request);
    }

    @PostMapping("")
    @Operation(
            summary = "Create currency",
            description = "Create a new currency record"
    )
    @ApiResponse(responseCode = "200", description = "Currency created successfully")
    public CommonResponse<Void> createCurrency(@RequestBody @Valid CurrencyCreateRequest request) {
        currencyService.saveCurrency(request);
        return CommonResponse.<Void>builder()
                .code(HttpStatus.CREATED.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .build();
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update currency",
            description = "Update an existing currency record by ID"
    )
    public CommonResponse<Void> saveCurrency(@PathVariable Long id, @RequestBody @Valid CurrencyUpdateRequest request) {
        currencyService.updateCurrency(id, request);
        return CommonResponse.<Void>builder()
                .code(HttpStatus.ACCEPTED.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete currency",
            description = "Delete a currency record by ID"
    )
    public CommonResponse<Void> deleteCurrency(@PathVariable Long id) {
        currencyService.deleteCurrency(id);
        return CommonResponse.<Void>builder()
                .code(HttpStatus.ACCEPTED.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .build();
    }

}
