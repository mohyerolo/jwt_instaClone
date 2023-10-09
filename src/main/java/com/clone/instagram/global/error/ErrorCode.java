package com.clone.instagram.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INTERNAL_SERVER_ERROR(500, "INTERNAL-SERVER-ERROR", "INTERNAL SERVER ERROR"),
    VALIDATION_ERROR(400, "VALIDATION-ERROR", "VALIDATION ERROR"),
    NOT_FOUND_ERROR(400, "NOT_FOUND", "NOT FOUND ERROR"),

    NEED_LOGIN(401, "NEED_LOGIN", "NEED LOGIN"),
    ACCEPTABLE_BUT_EXISTS(202, "EXISTS", "EXISTS");

    private final int status;
    private final String errorType;
    private final String message;
}
