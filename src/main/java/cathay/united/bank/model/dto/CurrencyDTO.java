package cathay.united.bank.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class CurrencyDTO {
    private Long id;

    @JsonProperty(value = "currency_code")
    private String currencyCode;

    @JsonProperty(value = "currency_name")
    private String currencyName;

    @JsonProperty(value = "country")
    private String country;

    @JsonProperty(value = "symbol")
    private String symbol;

    @JsonProperty(value = "created_at")
    private Instant createdAt;

    @JsonProperty(value = "updated_at")
    private Instant updatedAt;
}
