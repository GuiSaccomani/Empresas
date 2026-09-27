package com.gestao.backend.appointment.repository;

import com.gestao.backend.appointment.entity.Appointment;
import com.gestao.backend.appointment.entity.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    // SEGURANÇA MULTI-TENANCY: Todos os agendamentos de uma empresa específica.
    Page<Appointment> findAllByCompanyId(UUID companyId, Pageable pageable);

    // Busca um agendamento garantindo que é do Tenant correto (evita vazamento de dados).
    Optional<Appointment> findByIdAndCompanyId(UUID id, UUID companyId);

    // Consulta customizada para buscar os agendamentos de uma empresa em um intervalo de tempo (Ex: agenda do dia).
    @Query("SELECT a FROM Appointment a WHERE a.company.id = :companyId AND a.scheduledTime >= :start AND a.scheduledTime <= :end")
    List<Appointment> findAppointmentsByCompanyAndDateRange(
            @Param("companyId") UUID companyId, 
            @Param("start") LocalDateTime start, 
            @Param("end") LocalDateTime end);
    @Query("SELECT a FROM Appointment a WHERE a.status IN (:statuses) AND a.reminderSent = false AND a.scheduledTime >= :start AND a.scheduledTime <= :end")
    List<Appointment> findAppointmentsForReminder(
            @Param("statuses") List<AppointmentStatus> statuses,
            @Param("start") LocalDateTime start, 
            @Param("end") LocalDateTime end);
}
