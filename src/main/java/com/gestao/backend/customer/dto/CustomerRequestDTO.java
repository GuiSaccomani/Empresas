package com.gestao.backend.customer.dto;

import jakarta.validation.constraints.NotBlank;

public record CustomerRequestDTO(
    @NotBlank(message = "O nome é obrigatório")
    String name,
    
    String email,
    
    String phone
) {}
