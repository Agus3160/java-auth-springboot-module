package com.fiuni.distri.project.fiuni.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class ApiException extends ResponseStatusException {
    private final HttpStatus status;

    public ApiException(HttpStatus status, String reason) {
        super(status, reason);
        this.status = status;
    }

}
