package com.devluan.blog_api.infrastructure.security;

import lombok.Getter;
import com.devluan.blog_api.application.dto.token.TokenPair;
import com.devluan.blog_api.domain.user.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@Getter
public class JwtTokenService {
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final TokenBlacklistService tokenBlacklistService;

    @Value("${jwt.token.access-expiration}")
    private long accessTokenExpiresIn;

    @Value("${jwt.token.refresh-expiration}")
    private long refreshTokenExpiresIn;

    public JwtTokenService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder, TokenBlacklistService tokenBlacklistService) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    public TokenPair generateTokens(User user) {
        Instant now = Instant.now();

        String accessToken = generateToken(user.getEmail().value(), now, accessTokenExpiresIn, "access");
        String refreshToken = generateToken(user.getEmail().value(), now, refreshTokenExpiresIn, "refresh");

        return new TokenPair(accessToken, refreshToken, accessTokenExpiresIn);
    }

    private String generateToken(String subject, Instant now, long expiresIn, String type) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("blog-api")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .subject(subject)
                .claim("type", type)
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public boolean isTokenValid(String token) {
        if (tokenBlacklistService.isBlacklisted(token)) {
            return false;
        }
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return jwt.getExpiresAt().isAfter(Instant.now());
        } catch (JwtException e) {
            return false;
        }
    }

    public String getSubject(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return jwt.getSubject();
        } catch (JwtException e) {
            return null;
        }
    }

    public boolean isRefreshToken(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return "refresh".equals(jwt.getClaim("type"));
        } catch (JwtException e) {
            return false;
        }
    }

    public String generateAccessTokenFromRefreshToken(String refreshToken) {
        if (!isRefreshToken(refreshToken) || !isTokenValid(refreshToken)) {
            throw new JwtException("Invalid refresh token");
        }
        String subject = getSubject(refreshToken);
        if (subject == null) {
            throw new JwtException("Invalid refresh token subject");
        }
        Instant now = Instant.now();
        return generateToken(subject, now, accessTokenExpiresIn, "access");
    }
}
