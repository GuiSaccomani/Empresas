package com.gestao.backend.company.controller;

import com.gestao.backend.company.entity.Company;
import com.gestao.backend.company.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    // Endpoint PÚBLICO liberado no SecurityConfig para cadastro inicial do Tenant
    @PostMapping
    public ResponseEntity<?> createCompany(@RequestBody Company company) {
        if (companyRepository.findByEmail(company.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email já cadastrado.");
        }
        
        // Criptografando a senha antes de salvar no banco
        company.setPassword(passwordEncoder.encode(company.getPassword()));
        
        Company saved = companyRepository.save(company);
        return ResponseEntity.ok(saved);
    }
}
