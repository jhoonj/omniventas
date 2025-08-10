package com.optica.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt")
public record JwtProperties(
        String secret,     // HS256 shared secret
        String issuer      // expected issuer
) {}