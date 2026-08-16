package com.gestao.backend.company.repository;

import com.gestao.backend.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {
    
    // Busca empresa pelo documento (CNPJ/CPF) para validações de duplicidade
    Optional<Company> findByDocument(String document);
    
    // Busca empresa pelo email (Útil para futuro login/autenticação via JWT)
    Optional<Company> findByEmail(String email);
}
