package com.gestao.backend.customer.service;

import com.gestao.backend.company.repository.CompanyRepository;
import com.gestao.backend.core.security.SecurityUtils;
import com.gestao.backend.core.util.WhatsAppPhoneFormatter;
import com.gestao.backend.customer.dto.CustomerRequestDTO;
import com.gestao.backend.customer.dto.CustomerResponseDTO;
import com.gestao.backend.customer.entity.Customer;
import com.gestao.backend.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CompanyRepository companyRepository;

    @Transactional(readOnly = true)
    public Page<CustomerResponseDTO> listAll(Pageable pageable) {
        UUID companyId = SecurityUtils.getCurrentCompanyId();
        
        // Paginação com isolamento de Tenant
        return customerRepository.findAllByCompanyId(companyId, pageable)
                .map(CustomerResponseDTO::fromEntity);
    }

    @Transactional
    public CustomerResponseDTO create(CustomerRequestDTO dto) {
        UUID companyId = SecurityUtils.getCurrentCompanyId();
        
        var company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        // Aplica o utilitário do Mobile/WhatsApp
        String formattedPhone = null;
        if (dto.phone() != null && !dto.phone().isBlank()) {
            formattedPhone = WhatsAppPhoneFormatter.formatToE164(dto.phone());
        }

        var customer = Customer.builder()
                .company(company)
                .name(dto.name())
                .email(dto.email())
                .phone(formattedPhone)
                .build();

        customer = customerRepository.save(customer);
        return CustomerResponseDTO.fromEntity(customer);
    }
}
