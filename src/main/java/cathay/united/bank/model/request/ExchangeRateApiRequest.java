package cathay.united.bank.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateApiRequest {
    private String base;

    @Builder.Default
    private String start_date = LocalDate.now().withDayOfMonth(1).toString();

    @Builder.Default
    private String end_date = LocalDate.now().toString();

    @Builder.Default
    private String data_type = "chart";

    @Builder.Default
    private String quote = "USD";

}
