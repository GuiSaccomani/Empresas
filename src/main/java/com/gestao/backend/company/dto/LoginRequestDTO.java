package com.gestao.backend.company.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
    @NotBlank(message = "O email é obrigatório")
    String email,
    
    @NotBlank(message = "A senha é obrigatória")
    String password
) {}
