package com.devluan.blog_api.domain.exception;

public class InvalidUserDataException extends DomainException {
    public InvalidUserDataException(String message, String errorCode) {
        super(message, errorCode);
    }
}
