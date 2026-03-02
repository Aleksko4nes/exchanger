package ru.kocha.exchanger_v1.repository;

import ru.kocha.exchanger_v1.entities.Currency;
import ru.kocha.exchanger_v1.repository.connection.JdbcConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyRepositoryImpl implements CurrencyRepository {
    private final static String SELECT_ALl_CURRENCIES = """
            select id, code, fullname as name, sign from currencies
            """;

    private final static String SELECT_CURRENCY_BY_CODE = """
            SELECT id, code, fullname as name, sign FROM currencies WHERE code = ?
            """;

    private final static String ADD_NEW_CURRENCY = """
            INSERT INTO currencies (code, fullname, sign) VALUES (?, ?, ?)
            """;

    @Override
    public List<Currency> getAllCurrencies() throws SQLException {

        List<Currency> currencies = new ArrayList<>();

        try (Connection connection = JdbcConnection.getConnection();
             var ps = connection.prepareStatement(SELECT_ALl_CURRENCIES)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Currency currency = createCurrency(rs);
                    currencies.add(currency);
                }
            }
        }
        return currencies;
    }

    @Override
    public Optional<Currency> getCurrencyByCode(String code) throws SQLException {

        try (var connection = JdbcConnection.getConnection();
             var ps = connection.prepareStatement(SELECT_CURRENCY_BY_CODE)) {
            ps.setString(1, code);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(createCurrency(rs));
                }
                return Optional.empty();
            }
        }
    }

    @Override
    public Optional<Currency> addNewCurrency(String code, String fullname, String sign) throws SQLException {

        try (var connection = JdbcConnection.getConnection();
             var ps = connection.prepareStatement(ADD_NEW_CURRENCY, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, code);
            ps.setString(2, fullname);
            ps.setString(3, sign);

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                return Optional.empty();
            }
            Currency currency;
            try (var rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    currency = new Currency(rs.getLong(1), code, fullname, sign);
                } else {
                    return Optional.empty();
                }
            }
            return Optional.of(currency);
        }
    }

    private Currency createCurrency (ResultSet rs) throws SQLException {
        return new Currency(
                rs.getLong("id"),
                rs.getString("code"),
                rs.getString("name"),
                rs.getString("sign")
        );
    }
}
