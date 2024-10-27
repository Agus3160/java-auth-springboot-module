package com.fiuni.distri.project.fiuni.exceptions;

import com.fiuni.distri.project.fiuni.dto.ResponseDto;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHanlder {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ResponseDto> handleApiExeception(ApiException ex) {
        return new ResponseEntity<>(new ResponseDto(ex.getStatus().value(), ex.getMessage(), null), ex.getStatus());
    }

}
