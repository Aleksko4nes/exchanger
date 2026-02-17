package ru.kocha.exchanger_v1.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.kocha.exchanger_v1.entities.Currency;
import ru.kocha.exchanger_v1.repository.CurrencyRepository;
import ru.kocha.exchanger_v1.repository.CurrencyRepositoryImpl;
import ru.kocha.exchanger_v1.utils.ErrorHandler;
import ru.kocha.exchanger_v1.utils.Validator;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

    @WebServlet("/currency/*")
    public class CurrencyServlet extends HttpServlet {

        private ObjectMapper mapper;
        private CurrencyRepository repository;

        @Override
        public void init() throws ServletException {
            super.init();
            this.mapper = new ObjectMapper();
            this.repository = new CurrencyRepositoryImpl();
        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            try {
                Optional<String> code = Validator.validateCurrencyCode(req.getPathInfo());

                if (code.isEmpty()) {
                    ErrorHandler.sendError(HttpServletResponse.SC_BAD_REQUEST, "Код валюты отсутствует в адресе", resp);
                    return;
                }

                Optional<Currency> currency = repository.getCurrencyByCode(code.get());
                if (currency.isEmpty()) {
                    ErrorHandler.sendError(HttpServletResponse.SC_NOT_FOUND, "Валюта не найдена", resp);
                    return;
                }

                String jsonResponse = mapper.writeValueAsString(currency.get());
                resp.getWriter().println(jsonResponse);

            } catch (SQLException e) {
                ErrorHandler.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "База данных не доступна", resp);
            } catch (Exception e) {
                ErrorHandler.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage(), resp);
            }
        }
    }
