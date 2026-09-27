package com.gestao.backend.company.controller;

import com.gestao.backend.company.entity.Company;
import com.gestao.backend.company.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    // Endpoint PÚBLICO liberado no SecurityConfig para cadastro inicial do Tenant
    @PostMapping
    public ResponseEntity<?> createCompany(@RequestBody Company company) {
        Company saved = companyService.createCompany(company);
        return ResponseEntity.ok(saved);
    }
    @PutMapping("/slug")
    public ResponseEntity<?> updateSlug(
            @RequestBody java.util.Map<String, String> body,
            @org.springframework.security.core.annotation.AuthenticationPrincipal com.gestao.backend.core.security.CustomUserDetails user) {
        String newSlug = body.get("slug");
        if (newSlug == null || newSlug.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Slug inválido");
        }
        
        com.gestao.backend.company.entity.Company company = companyService.findById(user.getCompanyId());
        company.setSlug(newSlug.toLowerCase().replaceAll("[^a-z0-9-]", "-"));
        
        // Tratar duplicidade de forma simplificada
        try {
            companyService.save(company);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Slug já em uso");
        }
        
        return ResponseEntity.ok(company);
    }
}