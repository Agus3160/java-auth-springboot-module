package com.fiuni.distri.project.fiuni.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthMeResponseDto {
    String username;
    String email;
    String[] roles;
}
