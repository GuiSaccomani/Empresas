package com.gestao.backend.core.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class CustomUserDetails implements UserDetails {

    @Getter
    private final UUID id;
    
    private final String email;
    private final String password;
    
    @Getter
    private final UUID companyId;
    
    @Getter
    private final Role role;
    
    @Getter
    private final boolean usaAgenda;
    
    @Getter
    private final boolean usaFinanceiro;
    
    @Getter
    private final boolean usaClientes;

    public CustomUserDetails(UUID id, String email, String password, UUID companyId, Role role, boolean usaAgenda, boolean usaFinanceiro, boolean usaClientes) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.companyId = companyId;
        this.role = role;
        this.usaAgenda = usaAgenda;
        this.usaFinanceiro = usaFinanceiro;
        this.usaClientes = usaClientes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}