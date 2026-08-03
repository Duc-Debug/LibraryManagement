package org.example.librarymanagement.infrastructure.web.auth;


import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(
    @NotBlank(message = "Token cannot be blank")
    String token
) {}