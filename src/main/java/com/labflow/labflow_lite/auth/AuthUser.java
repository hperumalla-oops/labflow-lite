package com.labflow.labflow_lite.auth;

// What we put in the SecurityContext after the JWT is verified.
public record AuthUser(Long id, String username, Role role) {}