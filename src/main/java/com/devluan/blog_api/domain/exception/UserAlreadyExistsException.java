package com.devluan.blog_api.domain.exception;

public class UserAlreadyExistsException extends DomainException {
    public UserAlreadyExistsException(String message, String errorCode, Throwable throwable) {
        super(message, errorCode, throwable);
    }

    public UserAlreadyExistsException(String message, String emailAlreadyExists) {
        super(message, emailAlreadyExists);
    }
}
