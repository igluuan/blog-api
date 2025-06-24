package com.devluan.blog_api.domain.exception;

public class InvalidCredentialsException extends DomainException {
    public InvalidCredentialsException(String message, String errorCode) {
        super(message,errorCode);
    }
}
