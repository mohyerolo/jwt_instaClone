package com.clone.instagram.global.error;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ErrorResponseDto {
    private int status;
    private String errorType;
    private String message;

    public ErrorResponseDto(ErrorCode errorCode) {
        this.status = errorCode.getStatus();
        this.errorType = errorCode.getErrorType();
        this.message = errorCode.getMessage();
    }
    public ErrorResponseDto(ErrorCode errorCode, String message) {
        this.status = errorCode.getStatus();
        this.errorType = errorCode.getErrorType();
        this.message = message;
    }
}
