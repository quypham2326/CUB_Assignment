-- schema.sql
-- Create currencies table
CREATE TABLE IF NOT EXISTS currencies
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,
    currency_code
    VARCHAR
(
    3
) NOT NULL UNIQUE,
    currency_name VARCHAR
(
    255
) NOT NULL,
    country VARCHAR
(
    255
),
    symbol VARCHAR
(
    10
),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

-- Create index for currency_code for better query performance
CREATE INDEX IF NOT EXISTS idx_currency_code ON currencies(currency_code);

-- Create exchange_rates table
CREATE TABLE IF NOT EXISTS exchange_rates
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,
    base_currency
    VARCHAR
(
    3
) NOT NULL,
    quote_currency VARCHAR
(
    3
) NOT NULL,
    mid_rate DECIMAL
(
    15,
    6
) NOT NULL,
    average_bid DECIMAL
(
    15,
    6
),
    average_ask DECIMAL
(
    15,
    6
),
    high_bid DECIMAL
(
    15,
    6
),
    high_ask DECIMAL
(
    15,
    6
),
    low_bid DECIMAL
(
    15,
    6
),
    low_ask DECIMAL
(
    15,
    6
),
    rate_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_base_quote ON exchange_rates(base_currency, quote_currency);
CREATE INDEX IF NOT EXISTS idx_rate_date ON exchange_rates(rate_date DESC);
CREATE INDEX IF NOT EXISTS idx_base_currency ON exchange_rates(base_currency);

-- Create composite index for latest rate queries
CREATE INDEX IF NOT EXISTS idx_latest_rates ON exchange_rates(base_currency, quote_currency, rate_date DESC);