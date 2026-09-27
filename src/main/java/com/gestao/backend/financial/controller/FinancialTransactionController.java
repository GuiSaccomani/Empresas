package com.gestao.backend.financial.controller;

import com.gestao.backend.financial.dto.FinancialTransactionRequestDTO;
import com.gestao.backend.financial.dto.FinancialTransactionResponseDTO;
import com.gestao.backend.financial.service.FinancialTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/financial")
@RequiredArgsConstructor
public class FinancialTransactionController {

    private final FinancialTransactionService service;

    @GetMapping
    public ResponseEntity<Page<FinancialTransactionResponseDTO>> listAll(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        return ResponseEntity.ok(service.listAll(pageable));
    }

    // Retorna o saldo consolidado (ideal para o Dashboard do app mobile)
    @GetMapping("/balance")
    public ResponseEntity<Map<String, BigDecimal>> getBalance() {
        BigDecimal balance = service.calculateBalance();
        return ResponseEntity.ok(Map.of("currentBalance", balance));
    }

    @PostMapping
    public ResponseEntity<FinancialTransactionResponseDTO> create(@RequestBody @Valid FinancialTransactionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    // Endpoint para exportação do extrato em Excel (.xlsx)
    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportExcel() throws java.io.IOException {
        java.io.ByteArrayInputStream stream = service.generateFinancialReport();
        byte[] bytes = stream.readAllBytes();

        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=extrato_financeiro.xlsx")
                .contentType(org.springframework.http.MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(bytes);
    }
}
