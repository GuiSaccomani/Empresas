package com.gestao.backend.financial.dto;

import com.gestao.backend.financial.entity.FinancialTransaction;
import com.gestao.backend.financial.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record FinancialTransactionResponseDTO(
    UUID id,
    BigDecimal amount,
    TransactionType type,
    String description,
    LocalDate transactionDate
) {
    // Conversão segura de Entidade para DTO
    public static FinancialTransactionResponseDTO fromEntity(FinancialTransaction transaction) {
        return new FinancialTransactionResponseDTO(
            transaction.getId(),
            transaction.getAmount(),
            transaction.getType(),
            transaction.getDescription(),
            transaction.getTransactionDate()
        );
    }
}
