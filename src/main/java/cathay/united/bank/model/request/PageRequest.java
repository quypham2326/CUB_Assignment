package cathay.united.bank.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageRequest {
    @Builder.Default()
    private Integer page = 1;
    @Builder.Default()
    private Integer size = 10;

    private String sort;
    private Sort.Direction sortDirection;
}
