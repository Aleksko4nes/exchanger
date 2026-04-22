package ru.kocha.exchanger_v1.servlets;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.kocha.exchanger_v1.dto.request.ExchangeRateRequestDto;
import ru.kocha.exchanger_v1.dto.response.ExchangeRateResponseDto;
import ru.kocha.exchanger_v1.entities.ExchangeRate;
import ru.kocha.exchanger_v1.service.ExchangeRateService;
import ru.kocha.exchanger_v1.utils.ErrorHandler;
import ru.kocha.exchanger_v1.utils.RateParser;
import ru.kocha.exchanger_v1.utils.ServletResponseUtil;
import ru.kocha.exchanger_v1.utils.Validator;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

    private ExchangeRateService service;

    @Override
    public void init() {
        ServletContext servletContext = getServletContext();
        this.service = (ExchangeRateService) servletContext.getAttribute("exchangeRateService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<ExchangeRateResponseDto> responseDtos = service.getAllExchangeRates();
        ServletResponseUtil.sendSuccessMessage(resp, responseDtos);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ExchangeRateRequestDto requestDto = mapToDto(req);
        ExchangeRateResponseDto responseDto = service.addNewExchangeRate(requestDto);
        ServletResponseUtil.sendSuccessMessage(resp, responseDto);
    }

    private ExchangeRateRequestDto mapToDto (HttpServletRequest req) {
        String from = req.getParameter("baseCurrencyCode");
        String to = req.getParameter("targetCurrencyCode");
        String stringRate = req.getParameter("rate");

        Validator.validateCurrencyCode(from);
        Validator.validateCurrencyCode(to);
        BigDecimal rate = RateParser.parseRate(stringRate);
        return new ExchangeRateRequestDto(from, to, rate);
   }
}
