package com.gestao.backend.customer.dto;

import com.gestao.backend.customer.entity.Customer;
import java.util.UUID;

public record CustomerResponseDTO(
    UUID id,
    String name,
    String email,
    String phone
) {
    // Factory method para converter Entity em DTO de forma limpa
    public static CustomerResponseDTO fromEntity(Customer customer) {
        return new CustomerResponseDTO(
            customer.getId(),
            customer.getName(),
            customer.getEmail(),
            customer.getPhone()
        );
    }
}
