package com.devluan.blog_api.infrastructure.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public final class SLF4JLoggerService implements LoggerService {
    private static final Logger logger = LoggerFactory.getLogger(SLF4JLoggerService.class);

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void info(String message, String cause) {
        logger.info("{} | Cause: {}", message, cause);
    }

    @Override
    public void warn(String message, String cause) {
        logger.warn("{} | Cause: {}", message, cause);
    }

    @Override
    public void error(String message, String cause) {
        logger.error("{} | Cause: {}", message, cause);
    }

    @Override
    public void error(String message, String cause, Throwable throwable) {
        logger.error("{} | Cause: {} | Exception: {}", message, cause, throwable.getMessage(), throwable);
    }

    @Override
    public void debug(String message) {
        logger.debug(message);
    }

    @Override
    public void debug(String message, String cause) {
        logger.debug("{} | Cause: {}", message, cause);
    }
}