package cathay.united.bank.repository;

import cathay.united.bank.model.entity.CurrencyEntity;
import cathay.united.bank.model.entity.ExchangeRate;
import cathay.united.bank.model.request.ExchangeRateFilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    boolean existsByBaseCurrency(CurrencyEntity baseCurrency);

    @Query("""
                SELECT er
                FROM ExchangeRate er
                LEFT JOIN er.baseCurrency bc
                WHERE (:#{#request.startDate} IS NULL OR er.rateDate >= :#{#request.startDate})
                  AND (:#{#request.endDate} IS NULL OR er.rateDate <= :#{#request.endDate})
                  AND (:#{#request.currencyCode} IS NULL OR bc.currencyCode = :#{#request.currencyCode})
            """)
    Page<ExchangeRate> fetchExchangeRates(ExchangeRateFilterRequest request, Pageable pageable);

    List<ExchangeRate> findTop10ByBaseCurrencyOrderByRateDateDesc(CurrencyEntity baseCurrency);

    @Modifying
    @Query("DELETE FROM ExchangeRate e " +
           "WHERE e.rateDate BETWEEN :startDate AND :endDate")
    void removeByDateRange(@Param("startDate") Instant startDate,
                           @Param("endDate") Instant endDate);

    @Query("SELECT MAX(er.createdAt) FROM ExchangeRate er WHERE er.baseCurrency = :baseCurrency")
    Optional<Instant> findLatestUpdateTimeByBaseCurrency(@Param("baseCurrency") CurrencyEntity baseCurrency);

}
