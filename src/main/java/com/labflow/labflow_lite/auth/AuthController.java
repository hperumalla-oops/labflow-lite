package com.labflow.labflow_lite.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}
    public record TokenResponse(String token) {}

    private final AuthService authService;

    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest req) {
        return new TokenResponse(authService.login(req.username(), req.password()));
    }
}