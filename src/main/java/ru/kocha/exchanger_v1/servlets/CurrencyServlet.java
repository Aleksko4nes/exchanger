package ru.kocha.exchanger_v1.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.kocha.exchanger_v1.entities.Currency;
import ru.kocha.exchanger_v1.repository.CurrencyRepository;
import ru.kocha.exchanger_v1.repository.CurrencyRepositoryImpl;
import ru.kocha.exchanger_v1.service.CurrencyService;
import ru.kocha.exchanger_v1.utils.ErrorHandler;
import ru.kocha.exchanger_v1.utils.Validator;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

    @WebServlet("/currency/*")
    public class CurrencyServlet extends HttpServlet {

        private ObjectMapper mapper;
        private CurrencyService service;

        @Override
        public void init() throws ServletException {
            this.mapper = new ObjectMapper();
            ServletContext servletContext = getServletContext();
            this.service = (CurrencyService) servletContext.getAttribute("currencyService");
            if (this.service == null) {
                throw new ServletException("CurrencyService is null");
            }

        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            try {
                Optional<String> code = Validator.validateCurrencyCode(req.getPathInfo());
                if (code.isEmpty()) {
                    ErrorHandler.sendError(HttpServletResponse.SC_BAD_REQUEST, "Код валюты отсутствует в адресе", resp);
                    return;
                }
                Currency currency = service.getCurrencyByCode(code.get());
                mapper.writeValue(resp.getWriter(), currency);
                resp.setStatus(HttpServletResponse.SC_OK);
            } catch (Exception e) {
                ErrorHandler.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage(), resp);
            }
        }
    }
