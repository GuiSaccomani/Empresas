package com.gestao.backend.appointment.service;

import com.gestao.backend.appointment.dto.AppointmentRequestDTO;
import com.gestao.backend.appointment.dto.AppointmentResponseDTO;
import com.gestao.backend.appointment.entity.Appointment;
import com.gestao.backend.appointment.entity.AppointmentStatus;
import com.gestao.backend.appointment.repository.AppointmentRepository;
import com.gestao.backend.company.repository.CompanyRepository;
import com.gestao.backend.core.security.SecurityUtils;
import com.gestao.backend.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final CustomerRepository customerRepository;
    private final CompanyRepository companyRepository;

    @Transactional(readOnly = true)
    public Page<AppointmentResponseDTO> listAll(Pageable pageable) {
        UUID companyId = SecurityUtils.getCurrentCompanyId();
        // Retorna a lista paginada e converte a Entidade para DTO record
        return appointmentRepository.findAllByCompanyId(companyId, pageable)
                .map(AppointmentResponseDTO::fromEntity);
    }

    @Transactional
    public AppointmentResponseDTO create(AppointmentRequestDTO dto) {
        UUID companyId = SecurityUtils.getCurrentCompanyId();
        var company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        // Segurança máxima: garantir que o cliente passado no DTO pertence MESMO à empresa logada!
        var customer = customerRepository.findByIdAndCompanyId(dto.customerId(), companyId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado ou não pertence a esta empresa"));

        var appointment = Appointment.builder()
                .company(company)
                .customer(customer)
                .scheduledTime(dto.scheduledTime())
                .status(AppointmentStatus.SCHEDULED)
                .notes(dto.notes())
                .build();

        appointment = appointmentRepository.save(appointment);
        return AppointmentResponseDTO.fromEntity(appointment);
    }
}
