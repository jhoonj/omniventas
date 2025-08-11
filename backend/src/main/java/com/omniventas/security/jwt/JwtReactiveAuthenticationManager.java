package com.omniventas.security.jwt;

import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collection;

@Component
public class JwtReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private static final Logger log = LoggerFactory.getLogger(JwtReactiveAuthenticationManager.class);
    private final JwtService jwtService;

    public JwtReactiveAuthenticationManager(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        log.debug("[AUTH] Entrando a authenticate() con clase: {}",
                authentication != null ? authentication.getClass().getName() : "null");

        if (!(authentication instanceof BearerTokenAuthenticationToken bearer)) {
            log.warn("[AUTH] No es BearerTokenAuthenticationToken. Devuelvo Mono.empty()");
            return Mono.empty();
        }

        log.debug("[AUTH] Token recibido (recortado): {}...",
                bearer.getToken().substring(0, Math.min(20, bearer.getToken().length())));

        return Mono.fromCallable(() -> {
                    Claims claims = jwtService.parseAndValidate(bearer.getToken());
                    log.debug("[AUTH] Claims parseados: {}", claims);

                    Collection<String> roles = jwtService.extractRoles(claims);
                    log.debug("[AUTH] Roles extraídos del JWT: {}", roles);

                    var authorities = roles.stream()
                            .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                            .map(SimpleGrantedAuthority::new)
                            .toList();

                    log.debug("[AUTH] Authorities finales: {}", authorities);
                    log.debug("[AUTH] Usuario (subject) en token: {}", claims.getSubject());

                    // 👇 devolvemos Authentication (no UsernamePasswordAuthenticationToken “a secas”)
                    return (Authentication) new UsernamePasswordAuthenticationToken(
                            claims.getSubject(), null, authorities
                    );
                })
                .doOnError(err -> log.error("[AUTH] Error procesando token JWT", err));
    }
}
