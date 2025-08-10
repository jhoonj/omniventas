package com.optica.security.jwt;


import com.optica.usuario.infrastructure.UsuarioCustomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioCustomRepository usuarioCustomRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public Mono<String> login(String email, String rawPassword) {
        return usuarioCustomRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new InvalidCredentialsException()))
                .flatMap(user -> {
                    // user.getContrasenaHash() viene de tu dominio/entidad
                    boolean ok = passwordEncoder.matches(rawPassword, user.contrasenaHash());
                    if (!ok) return Mono.error(new InvalidCredentialsException());

                    // Derivar roles desde tu dominio. Ej: por rol_id
                    List<String> roles = mapRolIdToRoles(user.rol_id());
                    // subject = email (o id, lo que prefieras)
                    String token = jwtService.generateToken(user.email(), roles);
                    return Mono.just(token);
                });
    }

    private List<String> mapRolIdToRoles(Long rolId) {
        // Ajusta a tu tabla: 1=ADMIN, 2=USER, etc.

        if (rolId == 1) return List.of("ROLE_ADMIN");
        if (rolId == 2) return List.of("ROLE_VENDEDOR");
        if (rolId == 3) return List.of("ROLE_OPTOMETRISTA");
        if (rolId == 4) return List.of("ROLE_USUARIO");
        return List.of("ROLE_USUARIO");



    }



    // Excepción 401
    public static class InvalidCredentialsException extends RuntimeException { }
}
