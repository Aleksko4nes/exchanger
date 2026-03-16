Create table currencies (
    id BIGSERIAL primary key,
    code Varchar(4) not null unique,
    full_name varchar not null,
    sign varchar(3) not null
);

CREATE TABLE exchange_rate (
    id BIGSERIAL PRIMARY KEY,
    base_currency_code VARCHAR(4) NOT NULL,
    target_currency_code VARCHAR(4) NOT NULL,
    rate DECIMAL(10, 6) NOT NULL,
    UNIQUE (base_currency_code, target_currency_code),
    FOREIGN KEY (base_currency_code) REFERENCES currencies(code),
    FOREIGN KEY (target_currency_code) REFERENCES currencies(code)
);