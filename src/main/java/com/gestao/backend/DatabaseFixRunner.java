package com.gestao.backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DatabaseFixRunner implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== RUNNING DATABASE ENCODING FIX ===");
        try {
            jdbcTemplate.execute(
                "CREATE UNIQUE INDEX IF NOT EXISTS idx_unique_appointment_slot " +
                "ON appointments (company_id, scheduled_time) " +
                "WHERE status != 'CANCELLED';"
            );
            System.out.println("=== UNIQUE INDEX idx_unique_appointment_slot CREATED ===");

            int updated = jdbcTemplate.update(
                "UPDATE financial_transactions " +
                "SET description = REPLACE(REPLACE(description, 'Serviço', 'Serviço'), 'Serviço', 'Serviço') " +
                "WHERE description LIKE 'Servi%'"
            );
            
            int updatedAppointments = jdbcTemplate.update(
                "UPDATE appointments " +
                "SET notes = REPLACE(REPLACE(notes, 'Serviço', 'Serviço'), 'Serviço', 'Serviço') " +
                "WHERE notes LIKE 'Servi%'"
            );
            System.out.println("=== FIXED " + updated + " financial transactions and " + updatedAppointments + " appointments! ===");
        } catch (Exception e) {
            System.out.println("=== FAILED TO FIX DB: " + e.getMessage());
        }
    }
}