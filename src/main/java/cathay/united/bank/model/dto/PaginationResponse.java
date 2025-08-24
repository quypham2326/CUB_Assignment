package cathay.united.bank.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaginationResponse<T> {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    private Integer page;

    private Integer pageSize;

    private Integer totalPages;

    private Integer total;

}
