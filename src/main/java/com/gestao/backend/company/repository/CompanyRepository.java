package com.gestao.backend.company.repository;

import com.gestao.backend.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {
    
    Optional<Company> findByDocument(String document);
    Optional<Company> findByEmail(String email);
    Optional<Company> findBySlug(String slug);
    
    boolean existsByEmail(String email);
    boolean existsByDocument(String document);
}