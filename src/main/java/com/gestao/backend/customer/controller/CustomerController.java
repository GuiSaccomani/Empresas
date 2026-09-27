package com.gestao.backend.customer.controller;

import com.gestao.backend.customer.dto.CustomerRequestDTO;
import com.gestao.backend.customer.dto.CustomerResponseDTO;
import com.gestao.backend.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    // Recebe Pageable automaticamente do Spring via Query Params (?page=0&size=10)
    @GetMapping
    public ResponseEntity<Page<CustomerResponseDTO>> listAll(Pageable pageable) {
        return ResponseEntity.ok(customerService.listAll(pageable));
    }

    @PostMapping
    public ResponseEntity<CustomerResponseDTO> create(@RequestBody @Valid CustomerRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> update(@PathVariable java.util.UUID id, @RequestBody @jakarta.validation.Valid CustomerRequestDTO dto) {
        return ResponseEntity.ok(customerService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable java.util.UUID id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}