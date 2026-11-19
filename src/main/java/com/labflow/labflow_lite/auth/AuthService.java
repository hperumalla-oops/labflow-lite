package com.labflow.labflow_lite.auth;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(UserRepository users, PasswordEncoder encoder, JwtService jwt) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    public String login(String username, String password) {
        User user = users.findByUsername(username)
                .filter(u -> encoder.matches(password, u.getPasswordHash()))
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        return jwt.generate(user);
    }
}