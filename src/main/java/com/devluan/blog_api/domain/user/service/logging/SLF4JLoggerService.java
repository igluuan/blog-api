package com.devluan.blog_api.domain.user.service.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public abstract class SLF4JLoggerService implements LoggerService {
    private static final Logger logger = LoggerFactory.getLogger(SLF4JLoggerService.class);

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void warn(String message, String email) {
        logger.warn(message);
    }

    @Override
    public void error(String message, Throwable throwable) {
        logger.error(message, throwable);
    }

    @Override
    public void debug(String message) {
        logger.debug(message);
    }
}