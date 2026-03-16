package ru.kocha.exchanger_v1.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.kocha.exchanger_v1.dto.CurrencyRequestDto;
import ru.kocha.exchanger_v1.entities.Currency;
import ru.kocha.exchanger_v1.service.CurrencyService;
import ru.kocha.exchanger_v1.utils.Validator;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    private CurrencyService service;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        this.service = (CurrencyService) servletContext.getAttribute("currencyService");
        if (this.service == null) {
            throw new ServletException("currencyService is null");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try {
            CurrencyRequestDto currencyDto = mapToDto(req);
            Currency currency = service.createCurrency(currencyDto);
            mapper.writeValue(resp.getWriter(), currency);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/json");
        try {
            List<Currency> currencies = service.getCurrencies();
            mapper.writeValue(resp.getWriter(), currencies);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private CurrencyRequestDto mapToDto(HttpServletRequest req) {
        Optional<String> code = Validator.validateCurrencyCode(req.getParameter("code"));
        String fullName = req.getParameter("name");
        String sign = req.getParameter("sign");

        if (code.isEmpty() || fullName.isEmpty() || sign.isEmpty()) {
            throw new RuntimeException("Field is empty");
        }

        String validCode = code.get();
        return new CurrencyRequestDto(fullName, sign, validCode);
    }
}
