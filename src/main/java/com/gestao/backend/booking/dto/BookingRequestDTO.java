package com.gestao.backend.booking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookingRequestDTO {
    @NotBlank
    private String name;
    
    @Email
    private String email;
    
    private String phone;
    
    @NotNull
    private LocalDateTime scheduledTime;
}