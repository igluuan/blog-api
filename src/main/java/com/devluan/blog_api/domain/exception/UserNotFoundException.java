package com.devluan.blog_api.domain.exception;

public class UserNotFoundException extends DomainException {
    public UserNotFoundException(String message, String errorCode) {
        super(message, errorCode);
    }
}
