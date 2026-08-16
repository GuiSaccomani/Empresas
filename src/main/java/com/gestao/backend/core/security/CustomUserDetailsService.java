package com.gestao.backend.core.security;

import com.gestao.backend.company.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final CompanyRepository companyRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var company = companyRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Empresa não encontrada com email: " + username));
        return new CustomUserDetails(company);
    }
}
