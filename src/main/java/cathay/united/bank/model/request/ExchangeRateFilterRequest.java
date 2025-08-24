package cathay.united.bank.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneOffset;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateFilterRequest extends PageRequest {
    private String currencyCode;
    private Instant startDate;
    private Instant endDate;

    public void setStartDate(Instant startDate) {
        if (startDate != null) {
            this.startDate = startDate.atZone(ZoneOffset.UTC)
                    .toLocalDate()
                    .atStartOfDay(ZoneOffset.UTC)
                    .toInstant();
        } else {
            this.startDate = null;
        }
    }

    public void setEndDate(Instant endDate) {
        if (endDate != null) {
            this.endDate = endDate.atZone(ZoneOffset.UTC)
                    .toLocalDate()
                    .atTime(LocalTime.MAX)   // 23:59:59.999999999
                    .toInstant(ZoneOffset.UTC);
        } else {
            this.endDate = null;
        }
    }
}
