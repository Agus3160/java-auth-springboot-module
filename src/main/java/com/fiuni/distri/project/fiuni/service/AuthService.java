package com.fiuni.distri.project.fiuni.service;

import com.fiuni.distri.project.fiuni.auth.dto.AuthCredentialsDto;
import com.fiuni.distri.project.fiuni.auth.dto.AuthResponseDto;
import com.fiuni.distri.project.fiuni.dao.UserDao;
import com.fiuni.distri.project.fiuni.domain.Role;
import com.fiuni.distri.project.fiuni.domain.User;
import com.fiuni.distri.project.fiuni.exceptions.ApiException;
import com.fiuni.distri.project.fiuni.jwt.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDao userDao;

    //PERMITE OBTENER UN TOKEN POR MEDIO DE LAS CREDENCIALES
    public AuthResponseDto login(AuthCredentialsDto loginDto) {

        //Se utiliza el authentication Manager creado en la configuracion del SpringSecurity
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getEmail(),
                loginDto.getPassword()
        ));

        //Setea en el contexto, la authentication que contiene las credenciales
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //Se busca el usuario
        User user = userDao.findByEmail(loginDto.getEmail()).orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Bad Credentials"));

        //Se devuelve el token al usuario y se le pasa el userId a la funcion para que lo almacene como subject del token
        String token = jwtUtils.generateToken(authentication, user.getId());

        return new AuthResponseDto(user.getUsername(), user.getEmail(), user.getRoles().stream().map(Role::getRol).toArray(String[]::new), token);
    }

}
