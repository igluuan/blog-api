package com.devluan.blog_api.domain.user.service.logging;

public interface LoggerService {
    void info(String message, String info);

    void info(String message);

    void warn(String message, String email);
    void error(String message, String info, Throwable throwable);

    void error(String message, Throwable throwable);

    void debug(String message);
}
