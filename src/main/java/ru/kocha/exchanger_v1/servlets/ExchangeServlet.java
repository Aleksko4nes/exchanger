package ru.kocha.exchanger_v1.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.kocha.exchanger_v1.service.ExchangeService;
import ru.kocha.exchanger_v1.utils.ErrorHandler;
import ru.kocha.exchanger_v1.utils.RateParser;
import ru.kocha.exchanger_v1.utils.Validator;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    private ObjectMapper mapper;
    private ExchangeService service;

    @Override
    public void init() throws ServletException {
        super.init();
        this.mapper = new ObjectMapper();
        this.service = new ExchangeService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Optional<String> base = Validator.validateCurrencyCode(req.getParameter("from"));
            Optional<String> target = Validator.validateCurrencyCode(req.getParameter("to"));
            Optional<BigDecimal> amount = RateParser.parseRate(req.getParameter("amount"));
            if (base.isEmpty() || target.isEmpty() || amount.isEmpty()) {
                ErrorHandler.sendError(HttpServletResponse.SC_BAD_REQUEST, "Отсутствует нужное поле формы", resp);
                return;
            }

            String message = mapper.writeValueAsString(service.convert(base.get(), target.get(), amount.get()));
            PrintWriter out = resp.getWriter();
            out.println(message);
        } catch (SQLException e) {
            ErrorHandler.sendError(HttpServletResponse.SC_NOT_FOUND, "Валютная пара отсутствует в базе", resp);
        } catch (Exception e) {
            ErrorHandler.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка", resp);
        }
    }
}
