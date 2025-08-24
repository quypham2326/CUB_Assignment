package cathay.united.bank.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateDTO {

    @JsonProperty(value = "update_time")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private LocalDateTime updateTime;

    @JsonProperty(value = "exchange_rates")
    private List<ExchangeRateItemDTO> exchangeRateItemDTOS;
    
    @JsonProperty(value = "base_currency")
    private String baseCurrency;

    @JsonProperty(value = "total_records")
    private Integer totalRecords;
}
