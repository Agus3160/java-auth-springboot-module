package com.fiuni.distri.project.fiuni.config;

import com.fiuni.distri.project.fiuni.auth.CustomAccessDeniedHandler;
import com.fiuni.distri.project.fiuni.jwt.JwtAuthEntryPoint;
import com.fiuni.distri.project.fiuni.jwt.JwtAuthFilter;
import com.fiuni.distri.project.fiuni.service.AuthUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class AuthConfig {

    // USER SERVICE THAT IMPLEMENTS USER DETAILS TO MANAGE AUTH
    @Autowired
    AuthUserService authUserService;

    // GET THE PASSWORD ENCODER FORM A GLOBAL BEAN
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtAuthFilter jwtAuthFilter;

    @Autowired
    JwtAuthEntryPoint jwtAuthEntryPoint;

    @Autowired
    CustomAccessDeniedHandler accessDeniedHandler;

    @Value("${app.cors.allowedOrigins:*}")
    String allowedOrigins;

    //CONFIG OF THE PROCESS OF HOW A USER HAS TO AUTHENTICATE ITSELF (USING USER SERVICE AND USING AN PASSWORD ENCODER)
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(authUserService).passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(allowedOrigins.equals("*") ? Collections.singletonList("*") : List.of(allowedOrigins.split(","))); // Origen permitido
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    //CONFIG OF THE AUTH
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http

                //Turn off the csrf
                .csrf(AbstractHttpConfigurer::disable)

                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                //Designate the protected and public routes
                .authorizeHttpRequests(authReq ->
                        authReq
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .requestMatchers("auth/login", "auth/signup", "/error").permitAll()
                                .anyRequest().authenticated()
                )

                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint(jwtAuthEntryPoint).accessDeniedHandler(accessDeniedHandler)
                )


                //Set to stateless management policy
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
