package com.gestao.backend.company.controller;

import com.gestao.backend.company.dto.LoginRequestDTO;
import com.gestao.backend.company.dto.LoginResponseDTO;
import com.gestao.backend.company.dto.ForgotPasswordRequestDTO;
import com.gestao.backend.company.dto.ResetPasswordRequestDTO;
import com.gestao.backend.company.service.AuthService;
import com.gestao.backend.core.security.CustomUserDetails;
import com.gestao.backend.core.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints para login, token e recuperação de senha")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Realizar Login", description = "Autentica uma empresa e retorna o Token JWT.")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO request) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Esqueci minha senha", description = "Gera um token e envia link de recuperação para o e-mail informado (se existir).")
    public ResponseEntity<Void> forgotPassword(@RequestBody @Valid ForgotPasswordRequestDTO request) {
        authService.generatePasswordResetToken(request.email());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Redefinir senha", description = "Recebe o token de recuperação e aplica a nova senha.")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordRequestDTO request) {
        authService.resetPassword(request.token(), request.newPassword());
        return ResponseEntity.ok().build();
    }
}