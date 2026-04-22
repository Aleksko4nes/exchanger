package ru.kocha.exchanger_v1.servlets;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.kocha.exchanger_v1.dto.request.ExchangeRequest;
import ru.kocha.exchanger_v1.dto.response.ExchangeRateResponseDto;
import ru.kocha.exchanger_v1.dto.response.ExchangeResponse;
import ru.kocha.exchanger_v1.service.ExchangeService;
import ru.kocha.exchanger_v1.utils.ErrorHandler;
import ru.kocha.exchanger_v1.utils.RateParser;
import ru.kocha.exchanger_v1.utils.ServletResponseUtil;
import ru.kocha.exchanger_v1.utils.Validator;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    private ExchangeService service;

    @Override
    public void init() {
        ServletContext servletContext = getServletContext();
        this.service = (ExchangeService) servletContext.getAttribute("exchangeService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ExchangeRequest request = mapToRequestDto(req);
        ExchangeResponse responseDto = service.convert(request);
        ServletResponseUtil.sendSuccessMessage(resp, responseDto);
    }

    private ExchangeRequest mapToRequestDto (HttpServletRequest req) {
        String from = req.getParameter("from");
        String to = req.getParameter("to");
        String stringAmount = req.getParameter("amount");

        Validator.validateCurrencyCode(from);
        Validator.validateCurrencyCode(to);
        BigDecimal amount = RateParser.parseRate(stringAmount);

        return new ExchangeRequest(from, to, amount);
    }
}
