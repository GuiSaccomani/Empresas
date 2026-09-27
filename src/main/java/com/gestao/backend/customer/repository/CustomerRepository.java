package com.gestao.backend.customer.repository;

import com.gestao.backend.customer.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    
    // SEGURANÇA MULTI-TENANCY: Retorna apenas clientes pertencentes Ã  empresa informada.
    // PAGINAÇÃO: Otimizado para Mobile via Pageable
    Page<Customer> findAllByCompanyId(UUID companyId, Pageable pageable);
    
    // Busca um cliente específico garantindo que pertence Ã  empresa correta.
    Optional<Customer> findByIdAndCompanyId(UUID id, UUID companyId);
    
    // Busca cliente pelo telefone dentro de um Tenant (Essencial para receber Webhooks do WhatsApp).
    Optional<Customer> findByPhoneAndCompanyId(String phone, UUID companyId);
}
