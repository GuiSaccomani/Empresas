package com.gestao.backend.core.controller;

import com.gestao.backend.core.integration.ViaCepClient;
import com.gestao.backend.core.integration.ViaCepResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cep")
@RequiredArgsConstructor
public class CepController {

    private final ViaCepClient viaCepClient;

    // Endpoint auxiliar: Mobile usa para auto-completar o formulário de cadastro já em UPPERCASE
    @GetMapping("/{cep}")
    public ResponseEntity<ViaCepResponseDTO> consultarCep(@PathVariable String cep) {
        ViaCepResponseDTO response = viaCepClient.buscarCep(cep);
        
        ViaCepResponseDTO formattedResponse = new ViaCepResponseDTO(
            response.cep(),
            response.logradouro() != null ? response.logradouro().toUpperCase() : null,
            response.complemento() != null ? response.complemento().toUpperCase() : null,
            response.bairro() != null ? response.bairro().toUpperCase() : null,
            response.localidade() != null ? response.localidade().toUpperCase() : null,
            response.uf() != null ? response.uf().toUpperCase() : null,
            response.erro()
        );

        return ResponseEntity.ok(formattedResponse);
    }
}
