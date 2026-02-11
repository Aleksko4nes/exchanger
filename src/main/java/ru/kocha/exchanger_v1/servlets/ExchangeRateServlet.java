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
import java.util.Optional;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private ExchangeRateRepository repository;
    private ObjectMapper mapper;

    @Override
    public void init() throws ServletException {
        super.init();
        this.repository = new ExchangeRateRepositoryImpl();
        mapper = new ObjectMapper();
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Optional<String> path = Validator.validateExchangeRateCodes(req.getPathInfo());
            if (path.isEmpty()) {
                ErrorHandler.sendError(HttpServletResponse.SC_BAD_REQUEST, "Курсы валют отсутствуют в адресе," +
                        "либо пара задана не правильно", resp);
                return;
            }

            String from = path.get().substring(0,3);
            String to = path.get().substring(3);

            Optional<ExchangeRate> exchangeRate = repository.getExchangeRateByCode(from, to);
            if (exchangeRate.isEmpty()) {
                ErrorHandler.sendError(HttpServletResponse.SC_NOT_FOUND, "Обменный курс не найден", resp);
                return;
            }

            String message = mapper.writeValueAsString(exchangeRate.get());
            PrintWriter out = resp.getWriter();
            out.println(message);
        } catch (Exception e) {
            ErrorHandler.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage(), resp);
        }
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Optional<String> path = Validator.validateExchangeRateCodes(req.getPathInfo());
            if (path.isEmpty()) {
                ErrorHandler.sendError(HttpServletResponse.SC_BAD_REQUEST, "Курсы валют отсутствуют в адресе," +
                        "либо пара задана не правильно", resp);
                return;
            }

            String from = path.get().substring(0,3);
            String to = path.get().substring(3);
            Optional<BigDecimal> rate = RateParser.parseRate(req.getParameter("rate"));
            if (rate.isEmpty()) {
                ErrorHandler.sendError(HttpServletResponse.SC_BAD_REQUEST, "Отсутствует нужно поле формы", resp);
                return;
            }

            Optional<ExchangeRate> exchangeRate = repository.updateExchangerRate(from, to, rate.get());
            if (exchangeRate.isEmpty()) {
                ErrorHandler.sendError(HttpServletResponse.SC_NOT_FOUND, "Валютная пара отсутствует в базе данных", resp);
                return;
            }

            String message = mapper.writeValueAsString(exchangeRate.get());
            PrintWriter out = resp.getWriter();
            out.println(message);
        } catch (Exception e) {
            ErrorHandler.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage(), resp);
        }
    }
}
