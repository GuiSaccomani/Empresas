package com.gestao.backend.core.security;

import org.springframework.security.core.context.SecurityContextHolder;
import java.util.UUID;

public class SecurityUtils {

    /**
     * Extrai o ID da empresa (Tenant) a partir do contexto de segurança atual.
     * O JwtAuthenticationFilter já alimentou esse contexto na requisição atual.
     */
    public static UUID getCurrentCompanyId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Usuário não autenticado");
        }
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getCompanyId();
    }
}
