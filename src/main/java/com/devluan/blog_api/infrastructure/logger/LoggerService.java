package com.devluan.blog_api.infrastructure.logger;

public interface LoggerService {
    void info(String message);
    void info(String message, String cause);
    void warn(String message, String cause);
    void error(String message, String cause);
    void error(String message, String cause, Throwable throwable);
    void debug(String message);
    void debug(String message, String cause);
}