package com.fiuni.distri.project.fiuni.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiuni.distri.project.fiuni.exceptions.ResponseDto;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, JwtException, ServletException {

        String message = authException.getMessage();
        ResponseDto errorResponse = new ResponseDto<>(HttpServletResponse.SC_UNAUTHORIZED, message, null);
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }
}
