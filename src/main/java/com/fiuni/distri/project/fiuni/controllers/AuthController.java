package com.fiuni.distri.project.fiuni.controllers;

import com.fiuni.distri.project.fiuni.auth.dto.AuthCredentialsDto;
import com.fiuni.distri.project.fiuni.auth.dto.AuthMeResponseDto;
import com.fiuni.distri.project.fiuni.auth.dto.AuthResponseDto;
import com.fiuni.distri.project.fiuni.auth.AuthService;
import com.fiuni.distri.project.fiuni.domain.Role;
import com.fiuni.distri.project.fiuni.dto.ResponseDto;
import com.fiuni.distri.project.fiuni.dto.UserDto;
import com.fiuni.distri.project.fiuni.service.AuthUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    AuthUserService authUserService;

    @Autowired
    AuthService authService;

    @PostMapping("/login")
    public ResponseDto<AuthResponseDto> login(@RequestBody AuthCredentialsDto authCredentialsDto){
        AuthResponseDto authRes = authService.login(authCredentialsDto);
        return new ResponseDto<>(200, "Login successfull", authRes);
    }

    @PostMapping("/signup")
    public ResponseDto signup(@RequestBody UserDto userDto){
        System.out.println(userDto);
        authUserService.registerNewUser(userDto);
        return new ResponseDto<>(200, "SignUp successfully", null);
    }

    @GetMapping("/me")
    public ResponseDto<AuthMeResponseDto> getUserInfoByMainObject(){
        UserDto user = authUserService.getUserInfoByAuth();
        return new ResponseDto<>(
                200,
                "User data fetched successfully",
                new AuthMeResponseDto(user.getUsername(), user.getEmail(), user.getRoles())
        );
    }

}
