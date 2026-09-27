package com.gestao.backend.company.dto;

import lombok.Data;

@Data
public class EmployeeRequestDTO {
    private String name;
    private String email;
    private String password;
}