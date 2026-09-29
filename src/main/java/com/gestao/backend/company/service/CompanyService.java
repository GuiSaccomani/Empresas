package com.gestao.backend.company.service;

import com.gestao.backend.company.entity.Company;
import com.gestao.backend.company.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.gestao.backend.core.exception.DuplicateResourceException;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    public Company createCompany(Company company) {
        if (company.getName() != null) {
            company.setSlug(company.getName().toLowerCase().replaceAll("[^a-z0-9-]", "-") + "-" + java.util.UUID.randomUUID().toString().substring(0,4));
        }
        // 1. Sanitização
        if (company.getDocument() != null) {
            company.setDocument(company.getDocument().replaceAll("\\D", ""));
        }
        
        if (company.getAddress() != null && company.getAddress().getCep() != null) {
            company.getAddress().setCep(company.getAddress().getCep().replaceAll("\\D", ""));
        }

        // 2. Bloqueio de Duplicidade
        if (companyRepository.existsByEmail(company.getEmail())) {
            throw new DuplicateResourceException("Este E-mail já está cadastrado em nosso sistema.");
        }

        if (companyRepository.existsByDocument(company.getDocument())) {
            throw new DuplicateResourceException("Este CPF/CNPJ já está cadastrado em nosso sistema.");
        }

        // Criptografando a senha antes de salvar no banco
        company.setPassword(passwordEncoder.encode(company.getPassword()));

        return companyRepository.save(company);
    }
    public Company save(Company company) {
        return companyRepository.save(company);
    }

    public Company findById(java.util.UUID id) {
        return companyRepository.findById(id).orElseThrow(() -> new RuntimeException("Company not found"));
    }
}