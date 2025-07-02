package com.devluan.blog_api.domain.exception;

public class PostNotFoundException extends DomainException {
    public PostNotFoundException(String message, String errorCode) {
        super(message, errorCode);
    }
}