package ru.kocha.exchanger_v1.servlets;

import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.kocha.exchanger_v1.dto.response.CurrencyResponseDto;
import ru.kocha.exchanger_v1.service.CurrencyService;
import ru.kocha.exchanger_v1.utils.ServletResponseUtil;
import ru.kocha.exchanger_v1.utils.Validator;

import java.io.IOException;

    @WebServlet("/currency/*")
    public class CurrencyServlet extends HttpServlet {

        private CurrencyService service;

        @Override
        public void init() {
            ServletContext servletContext = getServletContext();
            this.service = (CurrencyService) servletContext.getAttribute("currencyService");
        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            String code = extractAndValidateCode(req);
            CurrencyResponseDto responseDto = service.getCurrencyByCode(code);
            ServletResponseUtil.sendSuccessMessage(resp, responseDto);
        }

        private String extractAndValidateCode(HttpServletRequest req) {
            String code = req.getParameter("code");
            Validator.validateCurrencyCode(code);
            return code;
        }
    }
