package com.devluan.blog_api.domain.exception;

import lombok.Getter;

@Getter
public class DomainException extends RuntimeException {
    private final String errorCode;

    public DomainException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    public DomainException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
