package com.fiuni.distri.project.fiuni.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiuni.distri.project.fiuni.exceptions.ApiException;
import com.fiuni.distri.project.fiuni.exceptions.ResponseDto;
import com.fiuni.distri.project.fiuni.service.AuthUserService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthUserService authUserService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Get JWT token from HTTP request
        String token = getTokenFromRequest(request);
        try {
            // Validate Token
            if (StringUtils.hasText(token) && jwtUtils.validateAccessToken(token)) {
                // get email from token
                String email = jwtUtils.getEmail(token);
                UserDetails userDetails = authUserService.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        token,
                        userDetails.getAuthorities()
                );

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

        } catch (JwtException e) {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            ResponseDto errorResponse = new ResponseDto<>(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage(), null);
            response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
            return;
        }catch (ApiException e){
            response.setContentType("application/json");
            response.setStatus(e.getStatus().value());

            ResponseDto errorResponse = new ResponseDto<>(e.getStatus().value(), e.getMessage(), null);
            response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            System.out.println(bearerToken);
            return bearerToken.substring(7);
        }

        return null;
    }

}