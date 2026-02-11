package ru.kocha.exchanger_v1.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.kocha.exchanger_v1.entities.ExchangeRate;
import ru.kocha.exchanger_v1.repository.ExchangeRateRepository;
import ru.kocha.exchanger_v1.repository.ExchangeRateRepositoryImpl;
import ru.kocha.exchanger_v1.utils.ErrorHandler;
import ru.kocha.exchanger_v1.utils.RateParser;
import ru.kocha.exchanger_v1.utils.Validator;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

    private ExchangeRateRepository repository;
    private ObjectMapper mapper;

    @Override
    public void init() throws ServletException {
        super.init();
        this.repository = new ExchangeRateRepositoryImpl();
        this.mapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            PrintWriter out = resp.getWriter();
            String message = mapper.writeValueAsString(repository.getExchangeRates());
            out.println(message);
        } catch (Exception e) {
            ErrorHandler.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка", resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Optional<String> baseCurrencyCode = Validator.validateCurrencyCode(req.getParameter("baseCurrencyCode"));
            Optional<String> targetCurrencyCode = Validator.validateCurrencyCode(req.getParameter("targetCurrencyCode"));
            Optional<BigDecimal> rate = RateParser.parseRate(req.getParameter("rate"));
            if (baseCurrencyCode.isEmpty() || targetCurrencyCode.isEmpty() || rate.isEmpty()) {
                ErrorHandler.sendError(HttpServletResponse.SC_BAD_REQUEST, "Отсутствует нужное поле формы", resp);
                return;
            }
            Optional<ExchangeRate> exchangeRate = repository.addNewExchangeRate(baseCurrencyCode.get(),
                                                                                targetCurrencyCode.get(),
                                                                                rate.get());
            if (exchangeRate.isEmpty()) {
                ErrorHandler.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Что-то пошло не так", resp);
                return;
            }

            PrintWriter out = resp.getWriter();
            String message = mapper.writeValueAsString(exchangeRate.get());
            out.println(message);

        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                ErrorHandler.sendError(HttpServletResponse.SC_CONFLICT, "Обменный курс уже существует", resp);
            } else {
                ErrorHandler.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "База данных не доступна", resp);
            }
        }
    }
}
