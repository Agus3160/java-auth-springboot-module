package com.fiuni.distri.project.fiuni.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthCredentialsDto {

    private String email;
    private String password;

}
