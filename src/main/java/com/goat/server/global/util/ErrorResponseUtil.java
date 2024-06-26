package com.goat.server.global.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goat.server.global.dto.ResponseTemplate;
import com.goat.server.global.exception.errorcode.ErrorCode;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ErrorResponseUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void setResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {

        ResponseTemplate<Object> errorResponse = ResponseTemplate.builder()
                .isSuccess(false)
                .code(errorCode.getHttpStatus().name())
                .message(errorCode.getMessage())
                .build();

        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
