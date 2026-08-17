package com.gestao.backend.company.repository;

import com.gestao.backend.company.entity.PasswordResetToken;
import com.gestao.backend.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByCompany(Company company);
    void deleteByCompany(Company company);
}