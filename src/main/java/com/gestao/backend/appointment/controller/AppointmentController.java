package com.gestao.backend.appointment.controller;

import com.gestao.backend.appointment.dto.AppointmentRequestDTO;
import com.gestao.backend.appointment.dto.AppointmentResponseDTO;
import com.gestao.backend.appointment.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    // Paginação injetada no parâmetro pelo Spring
    @GetMapping
    public ResponseEntity<Page<AppointmentResponseDTO>> listAll(Pageable pageable) {
        return ResponseEntity.ok(appointmentService.listAll(pageable));
    }

    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> create(@RequestBody @Valid AppointmentRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.create(dto));
    }
}
