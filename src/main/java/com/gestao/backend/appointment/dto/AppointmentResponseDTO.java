package com.gestao.backend.appointment.dto;

import com.gestao.backend.appointment.entity.Appointment;
import com.gestao.backend.appointment.entity.AppointmentStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentResponseDTO(
    UUID id,
    UUID customerId,
    String customerName,
    UUID employeeId,
    String employeeName,
    LocalDateTime scheduledTime,
    AppointmentStatus status,
    String notes
) {
    public static AppointmentResponseDTO fromEntity(Appointment appointment) {
        return new AppointmentResponseDTO(
            appointment.getId(),
            appointment.getCustomer().getId(),
            appointment.getCustomer().getName(),
            appointment.getEmployee() != null ? appointment.getEmployee().getId() : null,
            appointment.getEmployee() != null ? appointment.getEmployee().getName() : null,
            appointment.getScheduledTime(),
            appointment.getStatus(),
            appointment.getNotes()
        );
    }
}