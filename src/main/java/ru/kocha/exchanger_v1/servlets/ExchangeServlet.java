package ru.kocha.exchanger_v1.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.kocha.exchanger_v1.dto.ExchangeRequest;
import ru.kocha.exchanger_v1.dto.ExchangeResponse;
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
        ServletContext servletContext = getServletContext();
        this.mapper = new ObjectMapper();
        this.service = (ExchangeService) servletContext.getAttribute("exchangeService");
        if (this.service == null) {
            throw new ServletException("Service is null");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            resp.setContentType("application/json");
            ExchangeRequest request = mapToRequestDto(req);
            ExchangeResponse response = service.convert(
                    request.from(),
                    request.to(),
                    request.amount());

            mapper.writeValue(resp.getWriter(), response);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (SQLException e) {
            ErrorHandler.sendError(HttpServletResponse.SC_NOT_FOUND, "Валютная пара отсутствует в базе", resp);
        } catch (Exception e) {
            ErrorHandler.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка", resp);
        }
    }

    private ExchangeRequest mapToRequestDto (HttpServletRequest req) {
        Optional<String> base = Validator.validateCurrencyCode(req.getParameter("from"));
        Optional<String> target = Validator.validateCurrencyCode(req.getParameter("to"));
        Optional<BigDecimal> amount = RateParser.parseRate(req.getParameter("amount"));
        if (base.isEmpty() || target.isEmpty() || amount.isEmpty()) {
            throw new RuntimeException("Not enough parameters");
        }
        return new ExchangeRequest(base.get(), target.get(), amount.get());
    }
}
