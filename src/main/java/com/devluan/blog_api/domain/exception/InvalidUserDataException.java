package com.devluan.blog_api.domain.exception;

public class InvalidUserDataException extends DomainException {
    public InvalidUserDataException(String message, String errorCode, Throwable throwable) {
        super(message, errorCode, throwable);
    }

    public InvalidUserDataException(String requestCannotBeNull, String nullRequest) {
        super(requestCannotBeNull, nullRequest);
    }
}
