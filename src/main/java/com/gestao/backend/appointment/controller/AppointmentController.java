package com.gestao.backend.appointment.controller;

import com.gestao.backend.appointment.dto.AppointmentRequestDTO;
import com.gestao.backend.appointment.dto.AppointmentResponseDTO;
import com.gestao.backend.appointment.dto.CompleteAppointmentRequestDTO;
import com.gestao.backend.appointment.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import com.gestao.backend.booking.service.AppointmentReminderService;


@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {


    private final AppointmentService appointmentService;
    private final AppointmentReminderService reminderService;


    @GetMapping
    public ResponseEntity<Page<AppointmentResponseDTO>> listAll(Pageable pageable) {
        return ResponseEntity.ok(appointmentService.listAll(pageable));
    }

    @GetMapping("/range")
    public ResponseEntity<List<AppointmentResponseDTO>> getRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(appointmentService.getRange(start, end));
    }

    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> create(@RequestBody @Valid AppointmentRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> update(@PathVariable UUID id, @RequestBody @Valid AppointmentRequestDTO dto) {
        return ResponseEntity.ok(appointmentService.update(id, dto));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AppointmentResponseDTO> updateStatus(
            @PathVariable UUID id, 
            @RequestBody @Valid CompleteAppointmentRequestDTO dto) {
        return ResponseEntity.ok(appointmentService.updateStatus(id, dto));
    }

    @PostMapping("/{id}/send-reminder")
    public ResponseEntity<Void> sendReminder(@PathVariable UUID id) {
        reminderService.sendManualReminder(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        appointmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}