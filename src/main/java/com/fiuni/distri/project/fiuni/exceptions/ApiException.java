package com.fiuni.distri.project.fiuni.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ApiException extends ResponseStatusException {
    private final HttpStatus status;

    public ApiException(HttpStatus status, String reason) {
        super(status, reason);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return this.status;
    }
}
