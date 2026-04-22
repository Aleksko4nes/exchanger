package ru.kocha.exchanger_v1.servlets;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.kocha.exchanger_v1.dto.CurrencyCodePair;
import ru.kocha.exchanger_v1.dto.request.ExchangeRateRequestDto;
import ru.kocha.exchanger_v1.dto.response.ExchangeRateResponseDto;
import ru.kocha.exchanger_v1.service.ExchangeRateService;
import ru.kocha.exchanger_v1.utils.ErrorHandler;
import ru.kocha.exchanger_v1.utils.RateParser;
import ru.kocha.exchanger_v1.utils.ServletResponseUtil;
import ru.kocha.exchanger_v1.utils.Validator;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

    private ExchangeRateService service;

    @Override
    public void init() {
        ServletContext servletContext = getServletContext();
        this.service = (ExchangeRateService) servletContext.getAttribute("exchangeRateService");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getMethod().equals("PATCH")) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        CurrencyCodePair codePair = getCurrencyCodePair(req);
        ExchangeRateResponseDto responseDto = service.getExchangeRateByCodes(codePair);
        ServletResponseUtil.sendSuccessMessage(resp, responseDto);
    }


    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ExchangeRateRequestDto requestDto = mapToDto(req);
        ExchangeRateResponseDto responseDto = service.updateExchangeRate(requestDto);
        ServletResponseUtil.sendSuccessMessage(resp, responseDto);
    }

    private CurrencyCodePair getCurrencyCodePair(HttpServletRequest req) {
        String path = req.getPathInfo();
        System.out.println("path: " + path);
        Validator.validateExchangeRateCodes(path);
        String from = path.substring(1,4);
        String to = path.substring(4);
        return new CurrencyCodePair(from, to);
    }

    private ExchangeRateRequestDto mapToDto (HttpServletRequest req) throws IOException {
        CurrencyCodePair codePair = getCurrencyCodePair(req);
        String rateValue = extractRateFromBody(req);
        BigDecimal rate = RateParser.parseAndValidateRate(rateValue);
        return new ExchangeRateRequestDto(codePair.from(), codePair.to(), rate);
    }

    private String extractRateFromBody(HttpServletRequest req) throws IOException {
        BufferedReader reader = req.getReader();
        StringBuilder body = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            body.append(line);
        }
        String requestBody = body.toString();
        String[] pairs = requestBody.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2 && keyValue[0].equals("rate")) {
                return URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
            }
        }
        return null;
    }
}
