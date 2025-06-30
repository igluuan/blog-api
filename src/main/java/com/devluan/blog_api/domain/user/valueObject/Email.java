package com.devluan.blog_api.domain.user.valueObject;

import com.devluan.blog_api.domain.exception.InvalidUserDataException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;


@Embeddable
public record Email(@Column(name = "\"EMAIL\"") String value) {
    public Email {
        if (value == null || !value.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new InvalidUserDataException("Invalid email format", "INVALID_EMAIL_FORMAT" );
        }
    }
    public String getValue() {
        return value;
    }

}
