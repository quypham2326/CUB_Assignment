package cathay.united.bank.repository;

import cathay.united.bank.model.entity.CurrencyEntity;
import cathay.united.bank.model.request.CurrencyFilterRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CurrencyRepository extends JpaRepository<CurrencyEntity, Long> {

    Optional<CurrencyEntity> findByCurrencyCode(String code);

    @Query("""
            SELECT c FROM CurrencyEntity c
            WHERE (:#{#request.searchKey} IS NULL 
                   OR LOWER(c.currencyCode) LIKE LOWER(CONCAT('%', :#{#request.searchKey}, '%'))
                   OR LOWER(c.currencyName) LIKE LOWER(CONCAT('%', :#{#request.searchKey}, '%')))
            ORDER BY c.currencyCode
            """)
    List<CurrencyEntity> fetchCurrencies(CurrencyFilterRequest request);

}
