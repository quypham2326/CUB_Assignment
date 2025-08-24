package cathay.united.bank.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static cathay.united.bank.constant.ErrorMessageKeys.CURRENCY_CODE_PATTEN_UNMATCH;
import static cathay.united.bank.constant.ErrorMessageKeys.CURRENCY_CODE_REQUIRE;
import static cathay.united.bank.constant.ErrorMessageKeys.CURRENCY_NAME_REQUIRE;
import static cathay.united.bank.constant.ErrorMessageKeys.CURRENCY_NAME_SIZE;
import static cathay.united.bank.constant.ErrorMessageKeys.SYMBOL_SIZE;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyCreateRequest {

    @JsonProperty(value = "currency_code")
    @NotBlank(message = CURRENCY_CODE_REQUIRE)
    @Pattern(regexp = "^[A-Z]{3}$", message = CURRENCY_CODE_PATTEN_UNMATCH)
    private String currencyCode;

    @JsonProperty(value = "currency_name")
    @NotBlank(message = CURRENCY_NAME_REQUIRE)
    @Size(max = 255, message = CURRENCY_NAME_SIZE)
    private String currencyName;

    @JsonProperty(value = "country")
    private String country;

    @JsonProperty(value = "symbol")
    @Size(max = 10, message = SYMBOL_SIZE)
    private String symbol;
}
