package ru.kocha.exchanger_v1.servlets;

import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.kocha.exchanger_v1.dto.request.CurrencyRequestDto;
import ru.kocha.exchanger_v1.dto.response.CurrencyResponseDto;
import ru.kocha.exchanger_v1.service.CurrencyService;
import ru.kocha.exchanger_v1.utils.ServletResponseUtil;
import ru.kocha.exchanger_v1.utils.Validator;

import java.io.IOException;
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    private CurrencyService service;

    @Override
    public void init() {
        ServletContext servletContext = getServletContext();
        this.service = (CurrencyService) servletContext.getAttribute("currencyService");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            CurrencyRequestDto currencyDto = mapToDto(req);
            CurrencyResponseDto currency = service.createCurrency(currencyDto);
            ServletResponseUtil.sendSuccessMessage(resp, currency);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            List<CurrencyResponseDto> currencies = service.getCurrencies();
            ServletResponseUtil.sendSuccessMessage(resp, currencies);
    }

    private CurrencyRequestDto mapToDto(HttpServletRequest req) {
        String code = req.getParameter("code");
        String name = req.getParameter("name");
        String sign = req.getParameter("sign");

        Validator.validateCurrencyCode(code);
        Validator.validateCurrencyName(name);
        Validator.validateCurrencySign(sign);

        return new CurrencyRequestDto(code, name , sign);
    }
}
