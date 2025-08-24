package cathay.united.bank.model.mapper;

import cathay.united.bank.model.dto.CurrencyDTO;
import cathay.united.bank.model.dto.ExchangeRateItemDTO;
import cathay.united.bank.model.entity.CurrencyEntity;
import cathay.united.bank.model.entity.ExchangeRate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ExchangeRateMapper {
    @Mapping(source = "baseCurrency", target = "baseCurrency")
    ExchangeRateItemDTO mapperExchangeRateToDTO(ExchangeRate entity);

    CurrencyDTO mapperCurrencyToDTO(CurrencyEntity entity);

    List<ExchangeRateItemDTO> mapperExchangeRatesToDTOList(List<ExchangeRate> entities);
}
