package com.gestao.backend.core.integration;

public record ViaCepResponseDTO(
    String cep,
    String logradouro,
    String complemento,
    String bairro,
    String localidade,
    String uf,
    String erro // A API ViaCEP retorna {"erro": true} quando o CEP é inválido
) {}
