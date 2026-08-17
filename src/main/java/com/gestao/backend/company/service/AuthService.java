package com.gestao.backend.company.service;

import com.gestao.backend.company.entity.Company;
import com.gestao.backend.company.entity.PasswordResetToken;
import com.gestao.backend.company.repository.CompanyRepository;
import com.gestao.backend.company.repository.PasswordResetTokenRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final CompanyRepository companyRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    public AuthService(CompanyRepository companyRepository, 
                       PasswordResetTokenRepository tokenRepository, 
                       JavaMailSender mailSender,
                       PasswordEncoder passwordEncoder) {
        this.companyRepository = companyRepository;
        this.tokenRepository = tokenRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void generatePasswordResetToken(String email) {
        Optional<Company> companyOpt = companyRepository.findByEmail(email);
        if (companyOpt.isEmpty()) {
            return; // Retorna sem erro para não vazar emails cadastrados
        }

        Company company = companyOpt.get();
        
        // Remove token antigo se existir
        tokenRepository.deleteByCompany(company);

        // Cria novo token
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, company);
        tokenRepository.save(resetToken);

        // Envia email
        sendResetTokenEmail(company.getEmail(), token);
    }

    private void sendResetTokenEmail(String toEmail, String token) {
        String resetUrl = "http://localhost:5173/reset-password?token=" + token;
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Gestão PRO - Recuperação de Senha");
        message.setText("Você solicitou a recuperação da sua senha.\n\n" +
                "Clique no link abaixo para criar uma nova senha:\n" +
                resetUrl + "\n\n" +
                "Se você não solicitou isso, pode ignorar este email. O link expira em 2 horas.");
        
        mailSender.send(message);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido ou não encontrado."));

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new RuntimeException("O token expirou. Solicite um novo link.");
        }

        Company company = resetToken.getCompany();
        company.setPassword(passwordEncoder.encode(newPassword));
        companyRepository.save(company);
        
        tokenRepository.delete(resetToken);
    }
}