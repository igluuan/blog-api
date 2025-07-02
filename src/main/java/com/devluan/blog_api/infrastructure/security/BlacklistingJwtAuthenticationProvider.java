package com.devluan.blog_api.infrastructure.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;

public class BlacklistingJwtAuthenticationProvider implements AuthenticationProvider {

    private final TokenBlacklistService tokenBlacklistService;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    public BlacklistingJwtAuthenticationProvider(TokenBlacklistService tokenBlacklistService, JwtAuthenticationProvider jwtAuthenticationProvider) {
        this.tokenBlacklistService = tokenBlacklistService;
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        if (authentication.getCredentials() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getCredentials();
            if (tokenBlacklistService.isTokenBlacklisted(jwt.getTokenValue())) {
                throw new AuthenticationServiceException("Token blacklisted");
            }
        }
        return jwtAuthenticationProvider.authenticate(authentication);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return jwtAuthenticationProvider.supports(authentication);
    }
}