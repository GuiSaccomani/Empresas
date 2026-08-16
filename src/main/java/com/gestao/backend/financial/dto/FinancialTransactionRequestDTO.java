package com.gestao.backend.financial.dto;

import com.gestao.backend.financial.entity.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FinancialTransactionRequestDTO(
    @NotNull(message = "O valor é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    BigDecimal amount,

    @NotNull(message = "O tipo de transação (INCOME ou EXPENSE) é obrigatório")
    TransactionType type,

    @NotBlank(message = "A descrição é obrigatória")
    String description,

    @NotNull(message = "A data da transação é obrigatória")
    LocalDate transactionDate
) {}
