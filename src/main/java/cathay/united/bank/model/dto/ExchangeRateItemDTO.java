package cathay.united.bank.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateItemDTO {

    private Long id;

    @JsonProperty(value = "base_currency")
    private CurrencyDTO baseCurrency;

    @JsonProperty(value = "quote_currency")
    private String quoteCurrency;

    @JsonProperty(value = "mid_rate")
    private BigDecimal midRate;

    @JsonProperty(value = "average_bid")
    private BigDecimal averageBid;

    @JsonProperty(value = "average_ask")
    private BigDecimal averageAsk;

    @JsonProperty(value = "high_bid")
    private BigDecimal highBid;

    @JsonProperty(value = "high_ask")
    private BigDecimal highAsk;

    @JsonProperty(value = "low_bid")
    private BigDecimal lowBid;

    @JsonProperty(value = "low_ask")
    private BigDecimal lowAsk;

    @JsonProperty(value = "rate_date")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "UTC")
    private Instant rateDate;

    @JsonProperty(value = "created_at")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "UTC")
    private Instant createdAt;
}
