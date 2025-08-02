package com.pack.exception;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ErrorResponse {
    private String message;
    private String timestamp;
    private String path;

    public ErrorResponse(String message, String path){
        this.message=message;
        this.timestamp=LocalDateTime.now().toString();
        this.path=path;
    }
}

