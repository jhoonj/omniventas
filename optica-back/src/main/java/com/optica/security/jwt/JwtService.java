package com.optica.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Component
public class JwtService {

    private final SecretKey key;
    private final String expectedIssuer;

    public JwtService(JwtProperties props) {
        this.key = Keys.hmacShaKeyFor(props.secret().getBytes(StandardCharsets.UTF_8));
        this.expectedIssuer = props.issuer();
    }

    public Claims parseAndValidate(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        if (expectedIssuer != null && !expectedIssuer.equals(claims.getIssuer())) {
            throw new IllegalStateException("Issuer inválido");
        }
        return claims; // Exp, nbf, iat se validan automáticamente
    }

    @SuppressWarnings("unchecked")
    public Collection<String> extractRoles(Claims claims) {
        Object raw = claims.get("roles");

        if (raw == null) return List.of();

        // Caso común: ArrayList<String>
        if (raw instanceof Collection<?> c) {
            return c.stream().map(String::valueOf).toList();
        }

        // Alternativo: una sola cadena "ROLE_ADMIN,ROLE_USER"
        if (raw instanceof String s) {
            if (s.isBlank()) return List.of();
            return Arrays.stream(s.split("[,\\s]+")).map(String::trim).filter(t -> !t.isEmpty()).toList();
        }

        // Por si alguien metió objetos raros en roles
        return List.of(String.valueOf(raw));
    }

    public String generateToken(String username, Collection<String> roles) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(username)
                .issuer(expectedIssuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(Duration.ofHours(4)))) // expira en 4h
                .claim("roles", roles)
                .signWith(key) // HS256 por defecto con clave secreta
                .compact();
    }
}
