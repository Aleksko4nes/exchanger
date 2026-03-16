package ru.kocha.exchanger_v1.repository;

import ru.kocha.exchanger_v1.entities.Currency;
import ru.kocha.exchanger_v1.entities.ExchangeRate;
import ru.kocha.exchanger_v1.repository.connection.DataSource;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateRepositoryImpl implements ExchangeRateRepository {

    private final static String GET_ALL_EXCHANGE_RATES = """
            Select er.id,
            bc.id as base_id,
            bc.code as base_code,
            bc.full_name as base_fullname,
            bc.sign as base_sign,
            tc.id as target_id,
            tc.code as target_code,
            tc.full_name as target_fullname,
            tc.sign as target_sign,
            er.rate
            From exchange_rate er
            Join currencies bc ON bc.code = er.base_currency_code
            Join currencies tc ON tc.code = er.target_currency_code
            """;

    private final static String GET_EXCHANGE_RATE_BY_CODE = """
            Select er.id,
            bc.id as base_id,
            bc.code as base_code,
            bc.full_name as base_fullname,
            bc.sign as base_sign,
            tc.id as target_id,
            tc.code as target_code,
            tc.full_name as target_fullname,
            tc.sign as target_sign,
            er.rate
            From exchange_rate er
            Join currencies bc ON bc.code = er.base_currency_code
            Join currencies tc ON tc.code = er.target_currency_code
            Where bc.code = ? AND tc.code = ?
            """;

    private final static String ADD_NEW_EXCHANGE_RATE = """
            INSERT INTO exchange_rate (base_currency_code, target_currency_code, rate)
            VALUES (?, ?, ?)
            """;

    private final static String UPDATE_EXCHANGE_RATE = """
            UPDATE exchange_rate SET rate = ?
            WHERE base_currency_code = ?
            AND target_currency_code = ?
            """;

    private final static String GET_RATES_WITH_USD_BASE = """
            SELECT 
                er.id AS id,
                bc.id AS base_id,
                bc.code AS base_code,
                bc.full_name AS base_fullname,
                bc.sign AS base_sign,
                tc.id AS target_id,
                tc.code AS target_code,
                tc.full_name AS target_fullname,
                tc.sign AS target_sign,
                er.rate AS rate
                FROM exchange_rate er
                JOIN currencies bc ON er.base_currency_code = bc.code
                JOIN currencies tc ON er.target_currency_code = tc.code
                WHERE (
                    base_currency_code = 'USD' AND 
                    target_currency_code = ? OR
                    target_currency_code = ?
            """;

    @Override
    public List<ExchangeRate> getExchangeRates(Connection connection) throws SQLException {
        List<ExchangeRate> exchangeRates = new ArrayList<>();

        try (var ps = connection.prepareStatement(GET_ALL_EXCHANGE_RATES);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ExchangeRate exchangeRate = createExchangeRate(rs);
                exchangeRates.add(exchangeRate);
            }
            return exchangeRates;
        }
    }

    @Override
    public Optional<ExchangeRate> getExchangeRateByCode(String baseCode, String targetCode, Connection connection) throws SQLException {

        try (var ps = connection.prepareStatement(GET_EXCHANGE_RATE_BY_CODE)) {
            ps.setString(1, baseCode);
            ps.setString(2, targetCode);

            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(createExchangeRate(rs));
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    @Override
    public Optional<ExchangeRate> addNewExchangeRate(String baseCode, String targetCode, BigDecimal rate, Connection connection) throws SQLException {

        try (var ps = connection.prepareStatement(ADD_NEW_EXCHANGE_RATE)) {
            ps.setString(1, baseCode);
            ps.setString(2, targetCode);
            ps.setBigDecimal(3, rate);

            ps.executeUpdate();

            return getExchangeRateByCode(baseCode, targetCode, connection);
        }
    }

    @Override
    public Optional<ExchangeRate> updateExchangerRate(String baseCode, String targetCode, BigDecimal rate, Connection connection) {

        try (var ps = connection.prepareStatement(UPDATE_EXCHANGE_RATE)) {
            ps.setBigDecimal(1, rate);
            ps.setString(2, baseCode);
            ps.setString(3, targetCode);

            ps.executeUpdate();

            return getExchangeRateByCode(baseCode, targetCode, connection);

        } catch (SQLException e) {
            throw new RuntimeException("Something went wrong while updating new rate");
        }
    }

    @Override
    public List<ExchangeRate> findByCodeWithUsdBase(String baseCurrencyCode, String targetCurrencyCode, Connection connection) {

        List<ExchangeRate> rates = new ArrayList<>();
        try (var ps = connection.prepareStatement(GET_RATES_WITH_USD_BASE)) {
            ps.setString(1, baseCurrencyCode);
            ps.setString(2, targetCurrencyCode);

            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    ExchangeRate exchangeRate = createExchangeRate(rs);
                    rates.add(exchangeRate);
                }
                return rates;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Something went wrong while getting rate with base USD");
        }
    }

    private ExchangeRate createExchangeRate(ResultSet rs) throws SQLException {
        return new ExchangeRate(
                rs.getLong("id"),
                new Currency(
                        rs.getLong("base_id"),
                        rs.getString("base_code"),
                        rs.getString("base_fullname"),
                        rs.getString("base_sign")
                ),
                new Currency(
                        rs.getLong("target_id"),
                        rs.getString("target_code"),
                        rs.getString("target_fullname"),
                        rs.getString("target_sign")
                ),
                rs.getBigDecimal("rate")
        );
    }
}
