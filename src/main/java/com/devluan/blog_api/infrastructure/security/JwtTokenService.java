package com.devluan.blog_api.infrastructure.security;

import com.devluan.blog_api.application.dto.token.TokenPair;
import com.devluan.blog_api.domain.user.model.User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtTokenService {
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final long accessTokenExpiresIn = 3600L; // 1 hour
    private final long refreshTokenExpiresIn = 86400L; // 24 hours

    public JwtTokenService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
    }

    public TokenPair generateTokens(User user) {
        String accessToken = generateToken(user, accessTokenExpiresIn);
        String refreshToken = generateToken(user, refreshTokenExpiresIn);
        return new TokenPair(accessToken, refreshToken, accessTokenExpiresIn);
    }

    public String generateToken(User user, long expiresIn) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("blog-api")
                .subject(user.getEmail().value())
                .expiresAt(now.plusSeconds(expiresIn))
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public boolean isTokenValid(String token) {
        try {
            jwtDecoder.decode(token);
            return true;
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
            return jwt.getExpiresAt().getEpochSecond() - jwt.getIssuedAt().getEpochSecond() == refreshTokenExpiresIn;
        } catch (JwtException e) {
            return false;
        }
    }

    public String generateAccessTokenFromRefreshToken(String refreshToken) {
        String subject = getSubject(refreshToken);
        if (subject == null) {
            throw new JwtException("Invalid refresh token");
        }
        // Here you would typically fetch the user from the database using the subject
        // and then generate a new access token for that user.
        // For simplicity, we'll just generate a new token with the same subject.
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("blog-api")
                .subject(subject)
                .expiresAt(now.plusSeconds(accessTokenExpiresIn))
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public long getAccessTokenExpiresIn() {
        return accessTokenExpiresIn;
    }

    public long getRefreshTokenExpiresIn() {
        return refreshTokenExpiresIn;
    }
}
