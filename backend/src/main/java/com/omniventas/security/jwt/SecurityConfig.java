package com.omniventas.security.jwt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,
                                                            JwtReactiveAuthenticationManager jwtAuthManager,
                                                            BearerServerAuthenticationConverter bearerConverter) {
        var contextRepo = new WebSessionServerSecurityContextRepository();
        var jwtFilter = new AuthenticationWebFilter(jwtAuthManager);
        jwtFilter.setServerAuthenticationConverter(bearerConverter);
        jwtFilter.setSecurityContextRepository(contextRepo);

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .cors(cors -> {})
                .securityContextRepository(contextRepo)
                .authorizeExchange(ex -> ex
                        .pathMatchers(HttpMethod.GET, "/api/productos/{productoUid}/imagenes/{imagenUid}/raw").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/productos", "/api/productos/**").permitAll()
                        .pathMatchers("/api/productos/*/imagenes/**", "/v3/api-docs/**", "/swagger-ui/**", "/actuator/health","/webjars/**","/auth/login").permitAll()
                        .pathMatchers(HttpMethod.POST, "/usuarios", "/onmiventas/usuarios").hasRole("ADMIN")
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}
