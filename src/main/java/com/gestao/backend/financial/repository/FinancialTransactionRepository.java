package com.gestao.backend.financial.repository;

import com.gestao.backend.financial.entity.FinancialTransaction;
import com.gestao.backend.financial.entity.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FinancialTransactionRepository extends JpaRepository<FinancialTransaction, UUID> {

    // SEGURANÇA MULTI-TENANCY: Retorna o histórico paginado de caixa de uma empresa.
    Page<FinancialTransaction> findAllByCompanyId(UUID companyId, Pageable pageable);

    // Retorna todas as transações de uma empresa (para relatórios em Excel).
    List<FinancialTransaction> findAllByCompanyId(UUID companyId);

    // Valida que a transação manipulada realmente pertence à empresa que está fazendo a requisição.
    Optional<FinancialTransaction> findByIdAndCompanyId(UUID id, UUID companyId);

    // Consulta para consolidação financeira: Soma o total de ENTRADAS ou SAÍDAS de um Tenant.
    @Query("SELECT COALESCE(SUM(ft.amount), 0) FROM FinancialTransaction ft WHERE ft.company.id = :companyId AND ft.type = :type")
    BigDecimal sumAmountByCompanyAndType(
            @Param("companyId") UUID companyId, 
            @Param("type") TransactionType type);

    // Busca as movimentações de caixa de um dia específico para uma empresa (fechamento de caixa).
    List<FinancialTransaction> findAllByCompanyIdAndTransactionDate(UUID companyId, LocalDate date);
}
