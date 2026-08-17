package com.gestao.backend.company.dto;
import jakarta.validation.constraints.NotBlank;
public record ResetPasswordRequestDTO(
    @NotBlank String token,
    @NotBlank String newPassword
) {}