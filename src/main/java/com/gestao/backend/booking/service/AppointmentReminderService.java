package com.gestao.backend.booking.service;

import com.gestao.backend.appointment.entity.Appointment;
import com.gestao.backend.appointment.entity.AppointmentStatus;
import com.gestao.backend.appointment.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentReminderService {

    private final AppointmentRepository appointmentRepository;
    private final JavaMailSender mailSender;

    // Executa a cada hora cheia (ex: 13:00, 14:00)
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void sendReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.plusHours(23);
        LocalDateTime end = now.plusHours(24);

        List<AppointmentStatus> validStatuses = Arrays.asList(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED);
        
        List<Appointment> appointmentsToRemind = appointmentRepository.findAppointmentsForReminder(validStatuses, start, end);

        for (Appointment app : appointmentsToRemind) {
            String email = app.getCustomer().getEmail();
            if (email != null && !email.isBlank()) {
                try {
                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setTo(email);
                    message.setSubject("Lembrete de Agendamento - " + app.getCompany().getName());
                    
                    String dateStr = app.getScheduledTime().toLocalDate().toString();
                    String timeStr = app.getScheduledTime().toLocalTime().toString();
                    
                    message.setText(String.format(
                            "Olá %s,\n\n" +
                            "Este é um lembrete automático do seu agendamento em %s amanhã!\n\n" +
                            "Data: %s\n" +
                            "Horário: %s\n\n" +
                            "Caso não possa comparecer, por favor entre em contato com antecedência.",
                            app.getCustomer().getName().split(" ")[0],
                            app.getCompany().getName(),
                            dateStr,
                            timeStr
                    ));
                    
                    mailSender.send(message);
                    
                } catch (Exception e) {
                    System.err.println("Erro ao enviar email de lembrete para agendamento " + app.getId() + ": " + e.getMessage());
                    // Continua para o próximo mesmo que este falhe, porém não marcamos como sent, 
                    // para tentar na próxima rodada (já que a query é por hora). 
                    // Na verdade, como a janela é grande, talvez seja melhor marcar como true mesmo se falhar para não fazer spam, ou deixar tentar de novo.
                    // Vamos optar por marcar true apenas no final do bloco se for sucesso, ou deixar no banco.
                    // Neste caso de erro de rede, vamos pular a flag para ele tentar de novo na próxima hora se o erro resolver.
                    continue; 
                }
            }
            
            // Marca como enviado (mesmo se o cara não tiver email válido, para não bater na query toda hora de graça)
            app.setReminderSent(true);
            appointmentRepository.save(app);
        }
    }

}
