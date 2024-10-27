package com.fiuni.distri.project.fiuni.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiuni.distri.project.fiuni.dto.ResponseDto;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        ResponseDto errorResponse = new ResponseDto<>(HttpServletResponse.SC_FORBIDDEN, "You are not allowed to get this resource", null);
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }
}
