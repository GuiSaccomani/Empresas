package com.gestao.backend.booking.controller;

import com.gestao.backend.appointment.entity.Appointment;
import com.gestao.backend.booking.dto.BookingRequestDTO;
import com.gestao.backend.booking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public/{slug}")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getInfo(@PathVariable String slug) {
        return ResponseEntity.ok(bookingService.getCompanyInfo(slug));
    }

    @GetMapping("/available-slots")
    public ResponseEntity<List<LocalDateTime>> getAvailableSlots(
            @PathVariable String slug,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(bookingService.getAvailableSlots(slug, date));
    }

    @PostMapping("/appointments")
    public ResponseEntity<Map<String, Object>> createAppointment(
            @PathVariable String slug,
            @RequestBody @Valid BookingRequestDTO dto) {
        Appointment appointment = bookingService.createBooking(slug, dto);
        return ResponseEntity.ok(Map.of(
            "message", "Agendamento realizado com sucesso",
            "appointmentId", appointment.getId()
        ));
    }
}