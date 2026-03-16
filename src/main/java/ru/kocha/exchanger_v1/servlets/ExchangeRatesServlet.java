package ru.kocha.exchanger_v1.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.kocha.exchanger_v1.dto.ExchangeRateDto;
import ru.kocha.exchanger_v1.dto.ExchangeRateRequestDto;
import ru.kocha.exchanger_v1.dto.ExchangeResponse;
import ru.kocha.exchanger_v1.entities.ExchangeRate;
import ru.kocha.exchanger_v1.repository.ExchangeRateRepository;
import ru.kocha.exchanger_v1.repository.ExchangeRateRepositoryImpl;
import ru.kocha.exchanger_v1.service.ExchangeRateService;
import ru.kocha.exchanger_v1.utils.ErrorHandler;
import ru.kocha.exchanger_v1.utils.RateParser;
import ru.kocha.exchanger_v1.utils.Validator;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

    private ExchangeRateService service;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        this.service = (ExchangeRateService) servletContext.getAttribute("exchangeRateService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            resp.setContentType("application/json");
            List<ExchangeRate> exchangeRates = service.getAllExchangeRates();
            mapper.writeValue(resp.getWriter(), exchangeRates);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            ErrorHandler.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка", resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            ExchangeRateRequestDto exchangeRateDto = mapToDto(req);
            ExchangeRate response = service.addNewExchangeRate(exchangeRateDto);
            mapper.writeValue(resp.getWriter(), response);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            ErrorHandler.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка", resp);
        }
    }

    private ExchangeRateRequestDto mapToDto (HttpServletRequest req) {
        Optional<String> baseCodeOptional = Validator.validateCurrencyCode(req.getParameter("baseCurrencyCode"));
        if (baseCodeOptional.isEmpty()) {
            throw new ValidationException("Не верно указан код базовой валюты.");
        }

        Optional<String> targetCodeOptional = Validator.validateCurrencyCode(req.getParameter("targetCurrencyCode"));
        if (targetCodeOptional.isEmpty()) {
            throw new ValidationException("Не верно указан код целевой валюты");
        }

        Optional<BigDecimal> rateOptional = RateParser.parseRate(req.getParameter("rate"));
        if (rateOptional.isEmpty()) {
            throw new ValidationException("Что-то не так с рейтом на обмен");
        }

        String baseCode = baseCodeOptional.get();
        String targetCode = targetCodeOptional.get();
        BigDecimal rate = rateOptional.get();
        return new ExchangeRateDto(baseCode, targetCode, rate);
   }
}
