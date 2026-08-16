package com.gestao.backend.financial.service;

import com.gestao.backend.company.repository.CompanyRepository;
import com.gestao.backend.core.security.SecurityUtils;
import com.gestao.backend.core.util.ExcelGeneratorUtil;
import com.gestao.backend.financial.dto.FinancialTransactionRequestDTO;
import com.gestao.backend.financial.dto.FinancialTransactionResponseDTO;
import com.gestao.backend.financial.entity.FinancialTransaction;
import com.gestao.backend.financial.entity.TransactionType;
import com.gestao.backend.financial.repository.FinancialTransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FinancialTransactionService {

    private final FinancialTransactionRepository repository;
    private final CompanyRepository companyRepository;

    @Transactional(readOnly = true)
    public Page<FinancialTransactionResponseDTO> listAll(Pageable pageable) {
        UUID companyId = SecurityUtils.getCurrentCompanyId();
        
        // Paginação do extrato financeiro do Tenant
        return repository.findAllByCompanyId(companyId, pageable)
                .map(FinancialTransactionResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateBalance() {
        UUID companyId = SecurityUtils.getCurrentCompanyId();
        
        // Busca consolidação no banco, aproveitando o COALESCE para evitar nulls
        BigDecimal incomes = repository.sumAmountByCompanyAndType(companyId, TransactionType.INCOME);
        BigDecimal expenses = repository.sumAmountByCompanyAndType(companyId, TransactionType.EXPENSE);

        return incomes.subtract(expenses);
    }

    @Transactional
    public FinancialTransactionResponseDTO create(FinancialTransactionRequestDTO dto) {
        UUID companyId = SecurityUtils.getCurrentCompanyId();
        var company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada"));

        var transaction = FinancialTransaction.builder()
                .company(company)
                .amount(dto.amount())
                .type(dto.type())
                .description(dto.description())
                .transactionDate(dto.transactionDate())
                .build();

        transaction = repository.save(transaction);
        return FinancialTransactionResponseDTO.fromEntity(transaction);
    }

    @Transactional(readOnly = true)
    public ByteArrayInputStream generateFinancialReport() {
        UUID companyId = SecurityUtils.getCurrentCompanyId();
        
        // Busca todas as transações da empresa para o relatório Excel
        List<FinancialTransaction> transactions = repository.findAllByCompanyId(companyId);
        
        List<FinancialTransactionResponseDTO> dtoList = transactions.stream()
                .map(FinancialTransactionResponseDTO::fromEntity)
                .toList();

        return ExcelGeneratorUtil.transactionsToExcel(dtoList);
    }
}
