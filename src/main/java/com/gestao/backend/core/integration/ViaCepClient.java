package com.gestao.backend.core.integration;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ViaCepClient {

    private final RestTemplate restTemplate = new RestTemplate();

    public ViaCepResponseDTO buscarCep(String cep) {
        // Limpa a string deixando apenas números
        String cleanCep = cep.replaceAll("\\D", "");
        
        if (cleanCep.length() != 8) {
            throw new IllegalArgumentException("Formato de CEP inválido");
        }

        String url = "https://viacep.com.br/ws/" + cleanCep + "/json/";
        ViaCepResponseDTO response = restTemplate.getForObject(url, ViaCepResponseDTO.class);

        if (response != null && "true".equals(response.erro())) {
            throw new IllegalArgumentException("CEP não encontrado");
        }

        return response;
    }
}
