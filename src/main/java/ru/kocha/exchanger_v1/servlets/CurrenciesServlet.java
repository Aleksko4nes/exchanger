
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
import java.util.List;
import java.util.Optional;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    private CurrencyRepository repository;
    private ObjectMapper mapper;

    @Override
    public void init() throws ServletException {
        super.init();
        this.repository = new CurrencyRepositoryImpl();
        this.mapper = new ObjectMapper();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Optional<String> code = Validator.validateCurrencyCode(req.getParameter("code"));
            String fullname = req.getParameter("name");
            String sign = req.getParameter("sign");

            if (code.isEmpty() || fullname.isEmpty() || sign.isEmpty()) {
                ErrorHandler.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Отсутствует нужное поле формы или код валюты не прошёл валидацию",
                        resp);
                return;
            }

            String validCode = code.get();
            Optional<Currency> currency = repository.addNewCurrency(validCode, fullname, sign);

            if (currency.isPresent()) {
                String jsonResponse = mapper.writeValueAsString(currency);
                resp.getWriter().write(jsonResponse);
            }

        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                ErrorHandler.sendError(HttpServletResponse.SC_CONFLICT, "Валюта с таким кодом уже существует", resp);
            } else {
                ErrorHandler.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "База данных не доступна", resp);
            }
        }
        catch (Exception e) {
            ErrorHandler.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage(), resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<Currency> currencies = repository.getAllCurrencies();
            String jsonResponse = mapper.writeValueAsString(currencies);
            resp.getWriter().write(jsonResponse);
        } catch (IOException e) {
            ErrorHandler.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error", resp);
        } catch (SQLException e) {
            ErrorHandler.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "База данных не доступна", resp);
        }
    }
}
