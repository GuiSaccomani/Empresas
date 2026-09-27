package com.gestao.backend.appointment.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentRequestDTO(
    @NotNull(message = "O ID do cliente é obrigatório")
    UUID customerId,
    
    UUID employeeId,
    
    @NotNull(message = "A data/hora do agendamento é obrigatória")
    LocalDateTime scheduledTime,
    
    String notes
) {}