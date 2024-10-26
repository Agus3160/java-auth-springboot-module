package com.fiuni.distri.project.fiuni.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDto {

    private String email;
    private String accessToken;

}
