package com.fiuni.distri.project.fiuni.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.security.core.GrantedAuthority;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${app.jwt-secret:secret}")
    private String jwtSecret;

    @Value("${app-jwt-expiration-milliseconds:360000}")
    private long jwtExpirationDate;

    private Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String generateToken(Authentication authentication, int userId){

        String email = authentication.getName();

        // Obtener los roles del usuario
        String roles = getRolesByAuthentication(authentication);

        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

        return Jwts.builder()
                .subject(""+userId)
                .claim("email", email)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(expireDate)
                .signWith(key())
                .compact();
    }

    public String getRolesByAuthentication(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(role -> !role.isEmpty())
                .reduce((first, second) -> first + "," + second)
                .orElse("");
    }

    public String getEmail(String token) {
        return (String) getClaims(token).get("email");
    }


    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }

    public Object getClaim(String token, String key) {
        return getClaims(token).get(key);
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateAccessToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) key())
                    .build()
                    .parse(token);
            return true;
        }catch (JwtException e){
            String errorMessage;

            if (e instanceof ExpiredJwtException) {
                errorMessage = "El token JWT ha expirado.";
                logger.error(errorMessage, e.getMessage());
            } else if (e instanceof MalformedJwtException) {
                errorMessage = "El token JWT es inválido.";
                logger.error(errorMessage, e.getMessage());
            } else if (e instanceof SignatureException) {
                errorMessage = "La validación de la firma del token ha fallado.";
                logger.error(errorMessage, e.getMessage());
            } else {
                errorMessage = "Error en el procesamiento del token JWT.";
                logger.error(errorMessage, e.getMessage());
            }
            throw new JwtException(errorMessage, e);
        }
    }
}
