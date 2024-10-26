package com.fiuni.distri.project.fiuni.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
public class ResponseDto<T> {

    private int httpStatus;
    private String message;
    private String timeStamp;
    private T data = null;

    public ResponseDto( int httpStatus, String message, T data ) {
        this.data = data;
        this.httpStatus = httpStatus;
        this.message = message;
        this.timeStamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME);
    }

}
