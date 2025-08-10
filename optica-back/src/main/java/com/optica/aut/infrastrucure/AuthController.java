package com.optica.aut.infrastrucure;


import com.optica.auth.dto.LoginRequest;
import com.optica.auth.dto.LoginResponse;
import com.optica.security.jwt.AuthService;
import com.optica.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

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
