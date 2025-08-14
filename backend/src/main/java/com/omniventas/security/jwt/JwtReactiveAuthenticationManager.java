package com.omniventas.security.jwt;

import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private static final Logger log = LoggerFactory.getLogger(JwtReactiveAuthenticationManager.class);
    private final JwtService jwt;

    public JwtReactiveAuthenticationManager(JwtService jwt) {
        this.jwt = jwt;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        final String raw;
        if (authentication instanceof BearerTokenAuthenticationToken btat) {
            raw = btat.getToken();
        } else if (authentication.getCredentials() instanceof String s) {
            raw = s;
        } else {
            return Mono.empty();
        }

        return Mono.fromCallable(() -> jwt.parseAndValidate(raw))
                .map(jws -> {
                    Claims claims = jws.getPayload();
                    String username = claims.getSubject();

                    Object rolesObj = claims.get("roles");
                    var roles = (rolesObj instanceof java.util.Collection<?> col)
                            ? col.stream().map(Object::toString).toList()
                            : java.util.List.<String>of();

                    var authorities = roles.stream()
                            .map(org.springframework.security.core.authority.SimpleGrantedAuthority::new)
                            .toList();

                    // 👇 cast explícito para que la cadena sea Mono<Authentication>
                    return (Authentication) new UsernamePasswordAuthenticationToken(username, jws, authorities);
                })
                // Alternativa a la línea anterior: .cast(Authentication.class)
                .onErrorMap(e -> new org.springframework.security.authentication.BadCredentialsException("Invalid JWT", e));
    }

}
