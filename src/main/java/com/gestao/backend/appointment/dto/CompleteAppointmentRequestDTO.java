package com.gestao.backend.appointment.dto;

import com.gestao.backend.appointment.entity.AppointmentStatus;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CompleteAppointmentRequestDTO(
    @NotNull(message = "O novo status é obrigatório")
    AppointmentStatus status,
    
    BigDecimal amount
) {}