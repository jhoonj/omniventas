package com.omniventas.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Component
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    private final SecretKey key;
    private final String issuer;
    private final Duration accessTtl;
    private final JwtParser parser;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.issuer:}") String issuer,
            // TTL configurable (ISO-8601). Ej: PT4H = 4 horas. Por defecto 4h.
            @Value("${security.jwt.access-ttl:PT4H}") String accessTtl
    ) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("security.jwt.secret no configurado");
        }
        this.key = Keys.hmacShaKeyFor(secret.trim().getBytes(StandardCharsets.UTF_8));
        this.issuer = issuer == null ? "" : issuer.trim();
        this.accessTtl = parseDurationSafe(accessTtl, Duration.ofHours(4));

        var builder = Jwts.parser().verifyWith(this.key);
        if (!this.issuer.isBlank()) builder = builder.requireIssuer(this.issuer);
        this.parser = builder.build();

        log.info("[JWT] HS256 listo. issuer='{}', ttl={}",
                (this.issuer.isBlank() ? "<none>" : this.issuer), this.accessTtl);
    }

    /** Crea un JWT compacto (HS256) con subject y roles, expiración = accessTtl. */
    public String generateToken(String subject, List<String> roles) {
        Instant now = Instant.now();
        Instant exp = now.plus(this.accessTtl);
        return Jwts.builder()
                .subject(subject)
                .issuer(this.issuer.isBlank() ? null : this.issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .claim("roles", roles == null ? List.of() : roles)
                .signWith(this.key)
                .compact();
    }

    /** Parsea y valida un JWT 'compact'. Puede recibir el header completo "Bearer x.y.z". */
    public Jws<Claims> parseAndValidate(String rawHeaderValue) {
        String token = sanitize(rawHeaderValue);
        return parser.parseSignedClaims(token);
    }

    /** Quita 'Bearer ', comillas y cualquier whitespace. */
    public static String sanitize(String raw) {
        if (raw == null) throw new IllegalArgumentException("Missing Authorization token");
        String t = raw.trim();
        if (t.regionMatches(true, 0, "Bearer ", 0, 7)) t = t.substring(7);
        if (t.length() > 1 && t.startsWith("\"") && t.endsWith("\"")) t = t.substring(1, t.length() - 1);
        return t.replaceAll("\\s+", "");
    }

    private static Duration parseDurationSafe(String text, Duration fallback) {
        try { return Duration.parse(text); } catch (Exception ignore) { return fallback; }
    }
}
