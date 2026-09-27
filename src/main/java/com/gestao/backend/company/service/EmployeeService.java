package com.gestao.backend.company.service;

import com.gestao.backend.company.dto.EmployeeRequestDTO;
import com.gestao.backend.company.dto.EmployeeResponseDTO;
import com.gestao.backend.company.entity.Employee;
import com.gestao.backend.company.repository.CompanyRepository;
import com.gestao.backend.company.repository.EmployeeRepository;
import com.gestao.backend.core.security.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    public List<EmployeeResponseDTO> listEmployees(UUID companyId) {
        return employeeRepository.findByCompanyId(companyId)
                .stream()
                .map(EmployeeResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO dto, UUID companyId) {
        // Validação de duplicidade
        if (companyRepository.existsByEmail(dto.getEmail()) || employeeRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Já existe um usuário com este e-mail.");
        }

        Employee employee = Employee.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .companyId(companyId)
                .role(Role.ROLE_STAFF)
                .build();

        Employee saved = employeeRepository.save(employee);
        return EmployeeResponseDTO.fromEntity(saved);
    }
}