package cathay.united.bank.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;

@Entity
@Table(name = "exchange_rates")
@Getter
@Setter
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_currency", referencedColumnName = "currency_code", nullable = false,
            foreignKey = @ForeignKey(name = "fk_base_currency"))
    private CurrencyEntity baseCurrency;

    private String quoteCurrency;

    @Column(name = "mid_rate")
    private BigDecimal midRate;

    @Column(name = "average_bid")
    private BigDecimal averageBid;

    @Column(name = "average_ask")
    private BigDecimal averageAsk;

    @Column(name = "high_bid")
    private BigDecimal highBid;

    @Column(name = "high_ask")
    private BigDecimal highAsk;

    @Column(name = "low_bid")
    private BigDecimal lowBid;

    @Column(name = "low_ask")
    private BigDecimal lowAsk;

    @Column(name = "rate_date")
    private Instant rateDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now().atOffset(ZoneOffset.UTC).toInstant();
        this.createdAt = now;
    }

}
