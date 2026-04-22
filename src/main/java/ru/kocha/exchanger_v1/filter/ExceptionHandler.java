package ru.kocha.exchanger_v1.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import ru.kocha.exchanger_v1.exception.TransactionException;
import ru.kocha.exchanger_v1.exception.ValidationException;
import ru.kocha.exchanger_v1.utils.ServletResponseUtil;

import java.io.IOException;

@WebFilter("/*")
public class ExceptionHandler implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse resp = (HttpServletResponse) response;

        try {
            chain.doFilter(request, response);
        } catch (TransactionException e) {
            ServletResponseUtil.sendErrorMessage(resp, e.getCode(), e.getMessage());
        } catch (ValidationException e) {
            ServletResponseUtil.sendErrorMessage(resp,  400, e.getMessage());
        }
    }
}
