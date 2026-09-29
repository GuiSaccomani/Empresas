package com.gestao.backend.booking.service;

import com.gestao.backend.appointment.entity.Appointment;
import com.gestao.backend.appointment.entity.AppointmentStatus;
import com.gestao.backend.appointment.repository.AppointmentRepository;
import com.gestao.backend.booking.dto.BookingRequestDTO;
import com.gestao.backend.company.entity.Company;
import com.gestao.backend.company.repository.CompanyRepository;
import com.gestao.backend.customer.entity.Customer;
import com.gestao.backend.customer.repository.CustomerRepository;
import com.gestao.backend.notification.entity.Notification;
import com.gestao.backend.notification.repository.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class BookingService {

    private final CompanyRepository companyRepository;
    private final AppointmentRepository appointmentRepository;
    private final CustomerRepository customerRepository;
    private final JavaMailSender mailSender;
    private final NotificationRepository notificationRepository;

    public Map<String, Object> getCompanyInfo(String slug) {
        Company company = companyRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada"));
        
        return Map.of(
            "name", company.getName(),
            "slug", company.getSlug()
        );
    }

    public List<LocalDateTime> getAvailableSlots(String slug, LocalDate date) {
        Company company = companyRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada"));

        LocalDateTime startOfDay = date.atTime(LocalTime.of(8, 0));
        LocalDateTime endOfDay = date.atTime(LocalTime.of(18, 0));

        List<Appointment> existingAppointments = appointmentRepository.findAppointmentsByCompanyAndDateRange(
                company.getId(), startOfDay, endOfDay);

        List<LocalDateTime> occupiedSlots = existingAppointments.stream()
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
                .map(Appointment::getScheduledTime)
                .collect(Collectors.toList());

        List<LocalDateTime> availableSlots = new ArrayList<>();
        LocalDateTime currentSlot = startOfDay;

        while (currentSlot.isBefore(endOfDay)) {
            if (!occupiedSlots.contains(currentSlot) && currentSlot.isAfter(LocalDateTime.now())) {
                availableSlots.add(currentSlot);
            }
            currentSlot = currentSlot.plusMinutes(30);
        }

        return availableSlots;
    }

    @Transactional
    public Appointment createBooking(String slug, BookingRequestDTO dto) {
        Company company = companyRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada"));

        // Validar horário livre
        List<LocalDateTime> availableSlots = getAvailableSlots(slug, dto.getScheduledTime().toLocalDate());
        if (!availableSlots.contains(dto.getScheduledTime())) {
            throw new RuntimeException("Horário indisponível ou já agendado.");
        }

        // Buscar ou criar Customer
        Customer customer = null;
        if (dto.getPhone() != null && !dto.getPhone().isBlank()) {
            customer = customerRepository.findByPhoneAndCompanyId(dto.getPhone(), company.getId()).orElse(null);
        }
        
        if (customer == null) {
            customer = Customer.builder()
                    .company(company)
                    .name(dto.getName())
                    .email(dto.getEmail())
                    .phone(dto.getPhone())
                    .build();
            customer = customerRepository.save(customer);
        }

        Appointment appointment = Appointment.builder()
                .company(company)
                .customer(customer)
                .scheduledTime(dto.getScheduledTime())
                .status(AppointmentStatus.SCHEDULED)
                .notes("Agendado pelo site")
                .build();

        try {
            appointment = appointmentRepository.save(appointment);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new com.gestao.backend.booking.exception.HorarioIndisponivelException("Esse horário acabou de ser reservado por outra pessoa, escolha outro.");
        }

        // Disparo do email de confirmação imediata
        if (customer.getEmail() != null && !customer.getEmail().isBlank()) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(customer.getEmail());
                message.setSubject("Confirmação de Agendamento - " + company.getName());
                
                String dateStr = dto.getScheduledTime().toLocalDate().toString();
                String timeStr = dto.getScheduledTime().toLocalTime().toString();
                
                message.setText(String.format(
                        "Olá %s,\n\n" +
                        "Seu agendamento em %s foi confirmado com sucesso!\n\n" +
                        "Data: %s\n" +
                        "Horário: %s\n\n" +
                        "Agradecemos a preferência e aguardamos você.",
                        customer.getName().split(" ")[0],
                        company.getName(),
                        dateStr,
                        timeStr
                ));
                mailSender.send(message);
            } catch (Exception e) {
                System.err.println("Falha ao enviar email de confirmação para o cliente: " + e.getMessage());
                // Não falha o agendamento se o email der erro
            }
        }

        // 2. Notificacao no sistema para a empresa
        try {
            String dateStr = dto.getScheduledTime().toLocalDate().toString();
            String timeStr = dto.getScheduledTime().toLocalTime().toString();
            String message = String.format("Novo agendamento: %s em %s às %s", customer.getName(), dateStr, timeStr);
            
            Notification notification = Notification.builder()
                    .companyId(company.getId())
                    .message(message)
                    .build();
            notificationRepository.save(notification);
        } catch (Exception e) {
            System.err.println("Falha ao salvar notificacao: " + e.getMessage());
        }

        return appointment;
    }
}