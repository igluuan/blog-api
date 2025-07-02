package com.devluan.blog_api.domain.exception;

public class InvalidPostDataException extends DomainException {
    public InvalidPostDataException(String message, String errorCode) {
        super(message, errorCode);
    }
}