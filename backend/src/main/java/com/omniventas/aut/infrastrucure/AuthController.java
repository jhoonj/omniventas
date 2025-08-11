package com.omniventas.aut.infrastrucure;


import com.omniventas.auth.dto.LoginRequest;
import com.omniventas.auth.dto.LoginResponse;
import com.omniventas.security.jwt.AuthService;
import com.omniventas.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor

public class AuthController {

    private final JwtService jwtService;


    private final AuthService authService;

    @PostMapping("/login")
    public Mono<LoginResponse> login(@RequestBody LoginRequest request) {
        return authService.login(request.getEmail(), request.getPassword())
                .map(LoginResponse::new);
    }
}
