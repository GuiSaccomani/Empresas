package com.gestao.backend.company.dto;

import com.gestao.backend.company.entity.Employee;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class EmployeeResponseDTO {
    private UUID id;
    private String name;
    private String email;
    private String role;
    
    public static EmployeeResponseDTO fromEntity(Employee employee) {
        return EmployeeResponseDTO.builder()
                .id(employee.getId())
                .name(employee.getName())
                .email(employee.getEmail())
                .role(employee.getRole().name())
                .build();
    }
}