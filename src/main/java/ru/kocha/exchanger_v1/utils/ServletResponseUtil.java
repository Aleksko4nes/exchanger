package ru.kocha.exchanger_v1.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class ServletResponseUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    private ServletResponseUtil() {};

    public static void sendSuccessMessage(HttpServletResponse response, Object responseDto) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);

        String respMessage = mapper.writeValueAsString(responseDto);
        response.getWriter().write(respMessage);
    }

    public static void sendErrorMessage(HttpServletResponse response, int errorCode, String errorMessage) throws IOException {
        response.setContentType("application/json");
        response.setStatus(errorCode);

        Map<String, String> error = new HashMap<>();
        error.put("message", errorMessage);

        String respMessage = mapper.writeValueAsString(error);
        response.getWriter().write(respMessage);
    }
}
