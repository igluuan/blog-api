package com.devluan.blog_api.domain.exception;

public class UserAlreadyExistsException extends DomainException {
    public UserAlreadyExistsException(String message, String errorCode) {
        super(message, errorCode);
    }
}
