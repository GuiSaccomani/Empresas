package com.gestao.backend.company.controller;

import com.gestao.backend.company.dto.EmployeeRequestDTO;
import com.gestao.backend.company.dto.EmployeeResponseDTO;
import com.gestao.backend.company.service.EmployeeService;
import com.gestao.backend.core.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<List<EmployeeResponseDTO>> getEmployees(@AuthenticationPrincipal CustomUserDetails user) {
        // Somente ROLE_ADMIN consegue ver a equipe toda, porem o metodo ja esta protegido por config ou pre-auth
        // Mas podemos forçar aqui se quiser, ou deixar pro @PreAuthorize
        if (!user.getRole().name().equals("ROLE_ADMIN")) {
            return ResponseEntity.status(403).build();
        }
        
        List<EmployeeResponseDTO> employees = employeeService.listEmployees(user.getCompanyId());
        return ResponseEntity.ok(employees);
    }

    @PostMapping
    public ResponseEntity<EmployeeResponseDTO> createEmployee(
            @RequestBody EmployeeRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails user) {
        
        if (!user.getRole().name().equals("ROLE_ADMIN")) {
            return ResponseEntity.status(403).build();
        }

        EmployeeResponseDTO created = employeeService.createEmployee(request, user.getCompanyId());
        return ResponseEntity.ok(created);
    }
}