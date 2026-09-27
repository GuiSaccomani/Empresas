package com.gestao.backend.appointment.service;

import com.gestao.backend.appointment.dto.AppointmentRequestDTO;
import com.gestao.backend.appointment.dto.AppointmentResponseDTO;
import com.gestao.backend.appointment.dto.CompleteAppointmentRequestDTO;
import com.gestao.backend.appointment.entity.Appointment;
import com.gestao.backend.appointment.entity.AppointmentStatus;
import com.gestao.backend.appointment.repository.AppointmentRepository;
import com.gestao.backend.company.entity.Employee;
import com.gestao.backend.company.repository.CompanyRepository;
import com.gestao.backend.company.repository.EmployeeRepository;
import com.gestao.backend.core.security.SecurityUtils;
import com.gestao.backend.customer.repository.CustomerRepository;
import com.gestao.backend.financial.entity.FinancialTransaction;
import com.gestao.backend.financial.entity.TransactionType;
import com.gestao.backend.financial.repository.FinancialTransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final CustomerRepository customerRepository;
    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;
    private final FinancialTransactionRepository financialTransactionRepository;

    @Transactional(readOnly = true)
    public Page<AppointmentResponseDTO> listAll(Pageable pageable) {
        UUID companyId = SecurityUtils.getCurrentCompanyId();
        return appointmentRepository.findAllByCompanyId(companyId, pageable)
                .map(AppointmentResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> getRange(LocalDateTime start, LocalDateTime end) {
        UUID companyId = SecurityUtils.getCurrentCompanyId();
        return appointmentRepository.findAppointmentsByCompanyAndDateRange(companyId, start, end)
                .stream()
                .map(AppointmentResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public AppointmentResponseDTO create(AppointmentRequestDTO dto) {
        UUID companyId = SecurityUtils.getCurrentCompanyId();
        var company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada"));

        var customer = customerRepository.findByIdAndCompanyId(dto.customerId(), companyId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado ou não pertence a esta empresa"));

        Employee employee = null;
        if (dto.employeeId() != null) {
            employee = employeeRepository.findById(dto.employeeId())
                    .filter(e -> e.getCompanyId().equals(companyId))
                    .orElseThrow(() -> new EntityNotFoundException("Funcionário não encontrado ou não pertence a esta empresa"));
        }

        var appointment = Appointment.builder()
                .company(company)
                .customer(customer)
                .employee(employee)
                .scheduledTime(dto.scheduledTime())
                .status(AppointmentStatus.SCHEDULED)
                .notes(dto.notes())
                .build();

        appointment = appointmentRepository.save(appointment);
        return AppointmentResponseDTO.fromEntity(appointment);
    }

    @Transactional
    public AppointmentResponseDTO update(UUID id, AppointmentRequestDTO dto) {
        UUID companyId = SecurityUtils.getCurrentCompanyId();
        
        var appointment = appointmentRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new EntityNotFoundException("Agendamento não encontrado"));

        if (!appointment.getCustomer().getId().equals(dto.customerId())) {
            var customer = customerRepository.findByIdAndCompanyId(dto.customerId(), companyId)
                    .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado ou não pertence a esta empresa"));
            appointment.setCustomer(customer);
        }

        if (dto.employeeId() != null) {
            if (appointment.getEmployee() == null || !appointment.getEmployee().getId().equals(dto.employeeId())) {
                var employee = employeeRepository.findById(dto.employeeId())
                        .filter(e -> e.getCompanyId().equals(companyId))
                        .orElseThrow(() -> new EntityNotFoundException("Funcionário não encontrado ou não pertence a esta empresa"));
                appointment.setEmployee(employee);
            }
        } else {
            appointment.setEmployee(null);
        }

        appointment.setScheduledTime(dto.scheduledTime());
        appointment.setNotes(dto.notes());

        appointment = appointmentRepository.save(appointment);
        return AppointmentResponseDTO.fromEntity(appointment);
    }

    @Transactional
    public AppointmentResponseDTO updateStatus(UUID id, CompleteAppointmentRequestDTO dto) {
        UUID companyId = SecurityUtils.getCurrentCompanyId();
        var appointment = appointmentRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new EntityNotFoundException("Agendamento não encontrado"));

        appointment.setStatus(dto.status());
        appointment = appointmentRepository.save(appointment);

        if (dto.status() == AppointmentStatus.COMPLETED && dto.amount() != null) {
            FinancialTransaction transaction = FinancialTransaction.builder()
                    .company(appointment.getCompany())
                    .amount(dto.amount())
                    .type(TransactionType.INCOME)
                    .description("Serviço: " + appointment.getCustomer().getName())
                    .transactionDate(LocalDate.now())
                    .build();
            financialTransactionRepository.save(transaction);
        }

        return AppointmentResponseDTO.fromEntity(appointment);
    }

    @Transactional
    public void delete(UUID id) {
        UUID companyId = SecurityUtils.getCurrentCompanyId();
        
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Agendamento não encontrado"));
                
        if (!appointment.getCompany().getId().equals(companyId)) {
            throw new org.springframework.security.access.AccessDeniedException("Você não tem permissão para excluir este agendamento");
        }
        
        appointmentRepository.delete(appointment);
    }
}