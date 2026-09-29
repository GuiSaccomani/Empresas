package com.gestao.backend.company.dto;

public record LoginResponseDTO(
    String token,
    boolean usaAgenda,
    boolean usaFinanceiro,
    boolean usaClientes
) {}
